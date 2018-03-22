package backlog4s.interpreters

import java.io.File
import java.nio.ByteBuffer
import java.util.logging.Logger

import akka.actor.ActorSystem
import akka.http.javadsl.ClientTransport
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.stream.Materializer
import akka.stream.scaladsl.{FileIO, Sink, Source}
import backlog4s.datas.{AccessKey, ApiErrors, OAuth2Token}
import backlog4s.dsl.BacklogHttpOp.HttpF
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl._
import spray.json._
import cats.Monad
import cats.implicits._
import monix.reactive.Observable

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class AkkaHttpInterpret(optTransport: Option[ClientTransport])
                       (implicit actorSystem: ActorSystem, mat: Materializer,
                        override val exc: ExecutionContext)
  extends BacklogHttpInterpret[Future]
    with WithFutureCompletion {

  val logger: Logger = Logger.getLogger("Akka http")

  import backlog4s.formatters.SprayJsonFormats._

  implicit val monad = implicitly[Monad[Future]]

  val settings = optTransport.map { transport =>
    ConnectionPoolSettings(actorSystem).withConnectionSettings(
      ClientConnectionSettings(actorSystem).withTransport(transport)
    )
  }.getOrElse(ConnectionPoolSettings(actorSystem))

  private val http = Http()
  private val timeout = 10.seconds
  private val maxRedirCount = 20
  private val reqHeaders: Seq[HttpHeader] = Seq(
    headers.`User-Agent`("backlog4s"),
    headers.`Accept-Charset`(HttpCharsets.`UTF-8`)
  )

  private def createRequest(method: HttpMethod, query: HttpQuery): HttpRequest = {
    query.credentials match {
      case AccessKey(key) =>
        val uri = Uri(query.baseUrl + query.path).withQuery(Query(query.params + ("apiKey" -> key)))
        logger.info(s"Create HTTP request method: ${method.value} and uri: $uri")
        HttpRequest(
          method = method,
          uri = uri
        ).withHeaders(reqHeaders)
      case OAuth2Token(token) =>
        val uri = Uri(query.baseUrl + query.path).withQuery(Query(query.params))
        logger.info(s"Create HTTP request method: ${method.value} and uri: $uri")
        HttpRequest(
          method = method,
          uri = Uri(query.baseUrl + query.path).withQuery(Query(query.params))
        ).withHeaders(
          reqHeaders :+
            headers.Authorization(OAuth2BearerToken(token))
        )
    }
  }


  private def createRequest[Payload](method: HttpMethod, query: HttpQuery,
                                     payload: Payload, format: JsonFormat[Payload]): Future[HttpRequest] = {
    logger.info(s"Prepare request with payload $payload")
    val formData = FormData(
      payload.toJson(format).asJsObject.fields.map {
        case (key, JsString(value)) => key -> value
        case (key, value) => key -> value.toString()
      }
    )

    Marshal(formData).to[RequestEntity].map { entity =>
      createRequest(method, query).withEntity(entity)
    }
  }


  private def doRequest(request: HttpRequest): Future[Response[String]] = {
    logger.info(s"Execute request $request")
    for {
      response <- http.singleRequest(request, settings = settings)
      data <- response.entity.toStrict(timeout).map(_.data.utf8String)
      result = {
        val status = response.status.intValue()
        logger.info(s"Received response with status: $status")
        if (response.status.isFailure()) {
          if (status >= 400 && status < 500)
            Either.left(RequestError(data.parseJson.convertTo[ApiErrors]))
          else {
            Either.left(ServerDown)
          }
        } else {
          logger.info(s"Response data is $data")
          Either.right(data)
        }
      }
    } yield result
  }


  override def pure[A](a: A): Future[A] = Future.successful(a)

  override def parallel[A](prgs: scala.Seq[HttpF[A]]): Future[scala.Seq[A]] = {
    logger.info("Running request in parallel")
    Future.sequence(
      prgs.map(_.foldMap(this))
    ).map { result =>
      result
    }
  }


  override def create[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): Future[Response[A]] =
    for {
      request <- createRequest(HttpMethods.POST, query, payload, payloadFormat)
      serverResponse <- doRequest(request)
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
      request <- createRequest(HttpMethods.PUT, query, payload, payloadFormat)
      serverResponse <- doRequest(request)
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
    logger.info(s"Following redirection $req")
    http.singleRequest(req, settings = settings).flatMap { resp =>
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
    logger.info(s"Downloading $query")
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
          logger.info("Received data stream from server")
          val stream = Observable.fromReactivePublisher(
            serverResponse.entity.dataBytes
              .map(_.asByteBuffer)
              .runWith(Sink.asPublisher(true))
          )
          Future.successful(Either.right(stream))
        }
      }
    } yield response
  }

  override def upload[A](query: HttpQuery,
                         file: File,
                         format: JsonFormat[A]): Future[Response[A]] = {
    logger.info(s"Uploading ${file.getName} to $query")
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
