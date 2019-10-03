package com.github.chaabaj.backlog4s.interpreters

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.ClientTransport
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.stream.Materializer
import akka.stream.scaladsl.{FileIO, Sink, Source}
import com.github.chaabaj.backlog4s.datas.{AccessKey, ApiErrors, OAuth2Token}
import com.github.chaabaj.backlog4s.dsl._
import spray.json._
import cats.implicits._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}
import monix.eval.Task
import monix.reactive.Observable
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.reflect.ClassTag
import scala.util.control.NonFatal


class BacklogHttpDslOnAkka(optTransport: Option[ClientTransport] = None)
                          (implicit actorSystem: ActorSystem, mat: Materializer, exc: ExecutionContext) extends BacklogHttpDsl[Task] {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

  val settings = optTransport.map { transport =>
    ConnectionPoolSettings(actorSystem).withConnectionSettings(
      ClientConnectionSettings(actorSystem).withTransport(transport)
    )
  }.getOrElse(ConnectionPoolSettings(actorSystem))

  private val http = Http()
  private val timeout = 10.seconds
  private val maxRedirectCount = 20
  private val reqHeaders: Seq[HttpHeader] = Seq(
    headers.`User-Agent`("backlog4s"),
    headers.`Accept-Charset`(HttpCharsets.`UTF-8`)
  )

  /**
    * shutdown all connection pools
    * @return
    */
  def terminate(): Task[Unit] =
    Task.deferFuture(http.shutdownAllConnectionPools())


  private def createRequest(method: HttpMethod, query: HttpQuery): HttpRequest = {
    query.credentials match {
      case AccessKey(key) =>
        val uri = Uri(query.baseUrl + query.path).withQuery(Query(
          QueryParam.encodeAll(query.params) + ("apiKey" -> key))
        )
        logger.info(s"Create HTTP request method: ${method.value} and uri: $uri")
        HttpRequest(
          method = method,
          uri = uri
        ).withHeaders(reqHeaders)
      case OAuth2Token(token) =>
        val uri = Uri(query.baseUrl + query.path).withQuery(Query(QueryParam.encodeAll(query.params)))
        logger.info(s"Create HTTP request method: ${method.value} and uri: $uri")
        HttpRequest(
          method = method,
          uri = Uri(query.baseUrl + query.path).withQuery(Query(QueryParam.encodeAll(query.params)))
        ).withHeaders(
          reqHeaders :+
            headers.Authorization(OAuth2BearerToken(token))
        )
    }
  }

  private def createRequest[Payload](method: HttpMethod, query: HttpQuery,
                                     payload: Payload, format: JsonFormat[Payload]): Task[HttpRequest] = {
    logger.info(s"Prepare request with payload $payload")
    val formData = FormData(
      payload.toJson(format).asJsObject.fields.map {
        case (key, JsString(value)) => key -> value
        case (key, value) => key -> value.toString()
      }
    )

    Task.deferFuture(
      Marshal(formData).to[RequestEntity].map { entity =>
        createRequest(method, query).withEntity(entity)
      }
    )
  }

  private def doRequest(request: HttpRequest): Task[Response[String]] = {
    logger.info(s"Execute request $request")
    for {
      response <- Task.deferFuture(http.singleRequest(request, settings = settings))
      data <- Task.deferFuture(response.entity.toStrict(timeout).map(_.data.utf8String))
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

  private def parseJson[A](response: String, format: JsonFormat[A])
                          (implicit classTag: ClassTag[A]): A = {
    try {
      response.parseJson.convertTo[A](format)
    } catch {
      case NonFatal(ex) =>
        logger.error(s"Failed to parse json error: ${ex.getMessage}")
        logger.error(s"Stacktrace:")
        ex.printStackTrace()
        logger.error(s"Got from server $response")
        logger.error(s"Expected to format of $classTag")
        logger.error(s"This is probably a bug, please contact the maintainer of the library")
        throw ex
    }
  }

  private def followRedirect(req: HttpRequest, count: Int = 0): Task[HttpResponse] = {
    logger.info(s"Following redirection $req")
    Task.deferFuture(http.singleRequest(req, settings = settings)).flatMap { resp =>
      resp.status match {
        case StatusCodes.Found | StatusCodes.SeeOther => resp.header[headers.Location].map { loc =>
          resp.entity.discardBytes()
          val locUri = loc.uri
          val newUri = locUri
          val newReq = req.copy(
            uri = newUri,
            headers = reqHeaders
          )
          if (count < maxRedirectCount) followRedirect(newReq, count + 1) else Task.deferFuture(http.singleRequest(newReq))
        }.getOrElse(throw new RuntimeException(s"location not found on 302 for ${req.uri}"))
        case _ => Task(resp)
      }
    }
  }

  override def post[Payload, A](query: HttpQuery, payload: Payload)(implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): Task[Response[A]] =
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


  override def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): Task[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.GET, query))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response


  override def delete(query: HttpQuery): Task[Response[Unit]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.DELETE, query))
      response = serverResponse.map(_ => ())
    } yield response

  override def put[Payload, A](query: HttpQuery, payload: Payload)(implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): Task[Response[A]] =
    for {
      request <- createRequest(HttpMethods.PUT, query, payload, payloadFormat)
      serverResponse <- doRequest(request)
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  override def download(query: HttpQuery): Task[Response[ByteStream]] = {
    logger.info(s"Downloading $query")
    val request = createRequest(HttpMethods.GET, query)
    for {
      serverResponse <- followRedirect(request)
      response <- {
        val status = serverResponse.status.intValue()
        if (serverResponse.status.isFailure()) {
          if (status >= 400 && status < 500)
            Task.deferFuture(serverResponse.entity.toStrict(timeout))
              .map(_.data.utf8String)
              .map(data => Either.left(RequestError(data.parseJson.convertTo[ApiErrors])))
          else {
            serverResponse.entity.discardBytes()
            Task(Either.left(ServerDown))
          }
        } else {
          logger.info("Received data stream from server")
          val stream = Observable.fromReactivePublisher(
            serverResponse.entity.dataBytes
              .map(_.asByteBuffer)
              .runWith(Sink.asPublisher(true))
          )
          Task(Either.right(stream))
        }
      }
    } yield response
  }

  override def upload[A](query: HttpQuery, file: File)(implicit format: JsonFormat[A]): Task[Response[A]] = {
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
      entity <- Task.deferFuture(Marshal(formData).to[RequestEntity])
      request = createRequest(HttpMethods.POST, query).withEntity(entity)
      serverResponse <- doRequest(request)
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response
  }

}
