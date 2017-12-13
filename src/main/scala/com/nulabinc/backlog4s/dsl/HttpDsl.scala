package com.nulabinc.backlog4s.dsl

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.Materializer
import cats.effect.IO
import cats.free.Free
import cats.{InjectK, ~>}
import com.nulabinc.backlog4s.datas.ApiErrors
import com.nulabinc.backlog4s.dsl.HttpADT.{ByteStream, Bytes, Response}
import spray.json.JsonFormat
import cats.implicits._
import fs2.Stream
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

sealed trait HttpError
case class RequestError(errors: ApiErrors) extends HttpError
case object ServerDown extends HttpError

object HttpADT {
  type Bytes = String
  type Response[A] = Either[HttpError, A]
  type ByteStream = Stream[IO, ByteBuffer]
}

sealed trait HttpADT[A]
case class Get[A](query: HttpQuery, format: JsonFormat[A])
  extends HttpADT[Response[A]]
case class Post[Payload, A](
  query: HttpQuery,
  payload: Payload,
  format: JsonFormat[A],
  payloadFormat: JsonFormat[Payload]
)
  extends HttpADT[Response[A]]
case class Put[Payload, A](
  query: HttpQuery,
  payload: Payload,
  format: JsonFormat[A],
  payloadFormat: JsonFormat[Payload]
) extends HttpADT[Response[A]]
case class Delete(query: HttpQuery) extends HttpADT[Response[Unit]]
case class Download(query: HttpQuery)
  extends HttpADT[Response[ByteStream]]

class BacklogHttpOp[F[_]](implicit I: InjectK[HttpADT, F]) {

  type HttpF[A] = Free[F, A]

  def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): HttpF[Response[A]] =
    Free.inject[HttpADT, F](Get(query, format))

  def post[Payload, A](query: HttpQuery, payload: Payload)
             (implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): HttpF[Response[A]] =
    Free.inject[HttpADT, F](Post(query, payload, format, payloadFormat))

  def put[Payload, A](query: HttpQuery, payload: Payload)
            (implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): HttpF[Response[A]] =
    Free.inject[HttpADT, F](Put(query, payload, format, payloadFormat))

  def delete(query: HttpQuery): HttpF[Response[Unit]] =
    Free.inject[HttpADT, F](Delete(query))

  def download(query: HttpQuery): HttpF[Response[ByteStream]] =
    Free.inject[HttpADT, F](Download(query))
}

object BacklogHttpOp {
  implicit def httpOp[F[_]](implicit I: InjectK[HttpADT, F]): BacklogHttpOp[F] =
    new BacklogHttpOp[F]()
}

trait BacklogHttpInterpret[F[_]] extends ~>[HttpADT, F] {
  def get[A](query: HttpQuery, format: JsonFormat[A]): F[Response[A]]
  def create[Payload, A](query: HttpQuery,
                         payload: Payload,
                         format: JsonFormat[A],
                         payloadFormat: JsonFormat[Payload]): F[Response[A]]
  def update[Payload, A](query: HttpQuery,
                         payload: Payload,
                         format: JsonFormat[A],
                         payloadFormat: JsonFormat[Payload]): F[Response[A]]
  def delete(query: HttpQuery): F[Response[Unit]]
  def download(query: HttpQuery): F[Response[ByteStream]]

  override def apply[A](fa: HttpADT[A]): F[A] = fa match {
    case Get(query, format) => get(query, format)
    case Put(query, payload, format, payloadFormat) =>
      update(query, payload, format, payloadFormat)
    case Post(query, payload, format, payloadFormat) =>
      create(query, payload, format, payloadFormat)
    case Delete(query) => delete(query)
    case Download(query) => download(query)
  }
}

sealed trait Credentials
case class AccessKey(key: String) extends Credentials
case class OAuth2Token(token: String) extends Credentials

class AkkaHttpInterpret(baseUrl: String, credentials: Credentials)
                       (implicit actorSystem: ActorSystem, mat: Materializer,
                        exc: ExecutionContext) extends BacklogHttpInterpret[Future] {


  import com.nulabinc.backlog4s.formatters.SprayJsonFormats._
  import streamz.converter._

  private val http = Http()
  private val timeout = 10.seconds

  private def setupCredentials(httpRequest: HttpRequest, credentials: Credentials): HttpRequest =
    credentials match {
      case AccessKey(key) => httpRequest.copy(uri = httpRequest.uri)
      case OAuth2Token(token) => httpRequest.withHeaders(
        headers.Authorization(OAuth2BearerToken(token))
      )
    }

  private def createRequest(method: HttpMethod, query: HttpQuery): HttpRequest =
    credentials match {
      case AccessKey(key) =>
        HttpRequest(
          uri = Uri(baseUrl + query.path).withQuery(Query(query.params + ("apiKey" -> key)))
        )
      case OAuth2Token(token) =>
        HttpRequest(
          uri = Uri(baseUrl + query.path).withQuery(Query(query.params))
        ).withHeaders(headers.Authorization(OAuth2BearerToken(token)))
    }

  private def createRequest[Payload](method: HttpMethod, query: HttpQuery,
                               payload: Payload, format: JsonFormat[Payload]): HttpRequest =
    createRequest(method, query).withEntity(payload.toJson(format).compactPrint)

  private def doRequest(request: HttpRequest): Future[Response[Bytes]] =
    for {
      response <- http.singleRequest(setupCredentials(request, credentials))
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

  override def download(query: HttpQuery): Future[Response[ByteStream]] = {
    val request = createRequest(HttpMethods.GET, query)
    for {
      serverResponse <- http.singleRequest(request)
      response <- {
        val status = serverResponse.status.intValue()
        if (serverResponse.status.isFailure()) {
          if (status >= 400 && status < 500)
            serverResponse
              .entity.toStrict(timeout)
              .map(_.data.utf8String)
              .map(data => Either.left(RequestError(data.parseJson.convertTo[ApiErrors])))
          else {
            Future.successful(Either.left(ServerDown))
          }
        } else {
          val stream = serverResponse.entity.dataBytes.toStream().map(_.asByteBuffer)
          Future.successful(Either.right(stream))
        }
      }
    } yield response
  }

}
