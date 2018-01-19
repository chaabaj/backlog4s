package backlog4s.interpreters

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.Materializer
import akka.stream.scaladsl.{FileIO, Sink, Source}
import cats.effect.IO
import backlog4s.datas.{AccessKey, ApiErrors, Credentials, OAuth2Token}
import backlog4s.dsl.HttpADT.{ByteStream, Bytes, Response}
import backlog4s.dsl.{BacklogHttpInterpret, HttpQuery, RequestError, ServerDown}
import spray.json._
import cats.implicits._
import fs2.interop.reactivestreams._

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class AkkaHttpInterpret(baseUrl: String, credentials: Credentials)
                       (implicit actorSystem: ActorSystem, mat: Materializer,
                        exc: ExecutionContext) extends BacklogHttpInterpret[Future] {


  import backlog4s.formatters.SprayJsonFormats._

  private val http = Http()
  private val timeout = 10.seconds
  private val maxRedirCount = 20
  private val reqHeaders: Seq[HttpHeader] = Seq(
    headers.`User-Agent`("backlog4s"),
    headers.`Content-Type`(ContentTypes.`application/json`),
    headers.`Accept-Charset`(HttpCharsets.`UTF-8`)
  )

  private def createRequest(method: HttpMethod, query: HttpQuery): HttpRequest =
    credentials match {
      case AccessKey(key) =>
        HttpRequest(
          method = method,
          uri = Uri(baseUrl + query.path).withQuery(Query(query.params + ("apiKey" -> key))),
        ).withHeaders(reqHeaders)
      case OAuth2Token(token) =>
        HttpRequest(
          method = method,
          uri = Uri(baseUrl + query.path).withQuery(Query(query.params))
        ).withHeaders(
          reqHeaders :+
            headers.Authorization(OAuth2BearerToken(token))
        )
    }

  private def createRequest[Payload](method: HttpMethod, query: HttpQuery,
                                     payload: Payload, format: JsonFormat[Payload]): HttpRequest =
    createRequest(method, query).withEntity(payload.toJson(format).compactPrint)

  private def doRequest(request: HttpRequest): Future[Response[Bytes]] =
    for {
      response <- http.singleRequest(request)
      data <- response.entity.toStrict(timeout).map(_.data.utf8String)
      result = {
        val status = response.status.intValue()
        if (response.status.isFailure()) {
          if (status >= 400 && status < 500)
            Either.left(RequestError(data.parseJson.convertTo[ApiErrors]))
          else {
            Either.left(ServerDown)
          }
        } else {
          println(s"received $data")
          Either.right(data)
        }
      }
    } yield result

  override def pure[A](a: A): Future[A] = Future.successful(a)

  override def create[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): Future[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.POST, query, payload, payloadFormat))
      response = serverResponse
        .map { content =>
          // Hackfix to fix invalid usage of 204 error code returned by the api
          // So in case of no content we have an empty object
          // The user of this method should use Map object to make this hackfix work
          // I will prefer to not do this kind of hackfix and have it fixed properly
          // in the API side. POST should always return a response since it create content
          // on the server side. This will be removed once the API return the created object
          // on Add Star api call
          if (content.isEmpty) "{}" else content
        }
        .map(_.parseJson.convertTo[A](format))
    } yield response


  override def get[A](query: HttpQuery, format: JsonFormat[A]): Future[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.GET, query))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  override def update[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): Future[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.PUT, query, payload, payloadFormat))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  override def delete(query: HttpQuery): Future[Response[Unit]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.DELETE, query))
      response = serverResponse.map(_ => ())
    } yield response

  // Follow redirection in case of download files
  // Files can be stored in cloud service
  // So we need to manually follow redirection
  // Akka http is too low level to do this automatically
  // We protect from infinite redirection using count
  private def followRedirect(req: HttpRequest, count: Int = 0): Future[HttpResponse] = {
    http.singleRequest(req).flatMap { resp =>
      resp.status match {
        case StatusCodes.Found | StatusCodes.SeeOther => resp.header[headers.Location].map { loc =>
          resp.entity.discardBytes()
          val locUri = loc.uri
          val newUri = locUri
          val newReq = req.copy(
            uri = newUri,
            headers = reqHeaders
          )
          if (count < maxRedirCount) followRedirect(newReq, count + 1) else Http().singleRequest(newReq)
        }.getOrElse(throw new RuntimeException(s"location not found on 302 for ${req.uri}"))
        case _ => Future(resp)
      }
    }
  }

  override def download(query: HttpQuery): Future[Response[ByteStream]] = {
    val request = createRequest(HttpMethods.GET, query)
    for {
      serverResponse <- followRedirect(request)
      response <- {
        val status = serverResponse.status.intValue()
        if (serverResponse.status.isFailure()) {
          if (status >= 400 && status < 500)
            serverResponse
              .entity.toStrict(timeout)
              .map(_.data.utf8String)
              .map(data => Either.left(RequestError(data.parseJson.convertTo[ApiErrors])))
          else {
            serverResponse.entity.discardBytes()
            Future.successful(Either.left(ServerDown))
          }
        } else {
          val stream = serverResponse.entity.dataBytes
            .map(_.asByteBuffer)
            .runWith(Sink.asPublisher(true))
            .toStream[IO]
          Future.successful(Either.right(stream))
        }
      }
    } yield response
  }

  override def upload[A](query: HttpQuery,
                         file: File,
                         format: JsonFormat[A]): Future[Response[A]] = {
    val formData = Multipart.FormData(
      Source.single(
        Multipart.FormData.BodyPart(
          file.getName,
          HttpEntity(MediaTypes.`application/octet-stream`, file.length(), FileIO.fromPath(file.toPath)),
          Map(
            "name" -> "file",
            "filename" -> file.getName
          )
        )
      )
    )

    for {
      entity <- Marshal(formData).to[RequestEntity]
      request = createRequest(HttpMethods.POST, query).withEntity(entity)
      serverResponse <- doRequest(request)
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response
  }
}
