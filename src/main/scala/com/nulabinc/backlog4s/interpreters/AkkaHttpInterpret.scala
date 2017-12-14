package com.nulabinc.backlog4s.interpreters

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{HttpEncodings, OAuth2BearerToken}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import cats.effect.IO
import com.nulabinc.backlog4s.datas.ApiErrors
import com.nulabinc.backlog4s.dsl.HttpADT.{ByteStream, Bytes, Response}
import com.nulabinc.backlog4s.dsl.{BacklogHttpInterpret, HttpQuery, RequestError, ServerDown}
import spray.json._
import cats.implicits._
import fs2.interop.reactivestreams._
import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

sealed trait Credentials
case class AccessKey(key: String) extends Credentials
case class OAuth2Token(token: String) extends Credentials


class AkkaHttpInterpret(baseUrl: String, credentials: Credentials)
                       (implicit actorSystem: ActorSystem, mat: Materializer,
                        exc: ExecutionContext) extends BacklogHttpInterpret[Future] {


  import com.nulabinc.backlog4s.formatters.SprayJsonFormats._

  private val http = Http()
  private val timeout = 10.seconds
  private val maxRedirCount = 20
  private val reqHeaders: Seq[HttpHeader] = Seq(
    headers.`User-Agent`("backlog4jv2"),
    headers.`Content-Type`(ContentTypes.`application/json`),
    headers.`Accept-Charset`(HttpCharsets.`UTF-8`)
  )

  private def createRequest(method: HttpMethod, query: HttpQuery): HttpRequest =
    credentials match {
      case AccessKey(key) =>
        HttpRequest(
          uri = Uri(baseUrl + query.path).withQuery(Query(query.params + ("apiKey" -> key))),
        ).withHeaders(reqHeaders)
      case OAuth2Token(token) =>
        HttpRequest(
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
      response = serverResponse.map(_.parseJson.convertTo[A](format))
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

  private def followRedirect(req: HttpRequest, count: Int = 0): Future[HttpResponse] = {
    http.singleRequest(req).flatMap { resp =>
      resp.status match {
        case StatusCodes.Found | StatusCodes.SeeOther => resp.header[headers.Location].map { loc =>

          val locUri = loc.uri
          val newUri = req.uri.copy(scheme = locUri.scheme, authority = locUri.authority)
          val newReq = req.copy(
            uri = newUri.withQuery(req.uri.query()),
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
              .map { bytes =>
                println(bytes)
                bytes
              }
            .map(_.asByteBuffer)
            .runWith(Sink.asPublisher(true))
            .toStream[IO]
          Future.successful(Either.right(stream))
        }
      }
    } yield response
  }
}
