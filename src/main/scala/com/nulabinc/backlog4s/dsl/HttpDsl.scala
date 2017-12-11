package com.nulabinc.backlog4s.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.Materializer
import cats.free.Free
import cats.{InjectK, ~>}
import com.nulabinc.backlog4s.dsl.HttpADT.Bytes
import com.nulabinc.backlog4s.exceptions.BacklogApiException

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

object HttpADT {
  type Bytes = String
}

sealed trait HttpADT[A]
case class Get(query: HttpQuery) extends HttpADT[Bytes]
case class Post(query: HttpQuery, payload: Bytes) extends HttpADT[Bytes]
case class Put(query: HttpQuery, payload: Bytes) extends HttpADT[Bytes]
case class Delete(query: HttpQuery) extends HttpADT[Bytes]

class HttpOp[F[_]](implicit I: InjectK[HttpADT, F]) {

  type HttpF[A] = Free[F, A]

  def get(query: HttpQuery): HttpF[Bytes] =
    Free.inject[HttpADT, F](Get(query))

  def post(query: HttpQuery, payload: Bytes): HttpF[Bytes] =
    Free.inject[HttpADT, F](Post(query, payload))

  def put(query: HttpQuery, payload: Bytes): HttpF[Bytes] =
    Free.inject[HttpADT, F](Put(query, payload))

  def delete(query: HttpQuery): HttpF[Bytes] =
    Free.inject[HttpADT, F](Delete(query))
}

object HttpOp {
  implicit def httpOp[F[_]](implicit I: InjectK[HttpADT, F]): HttpOp[F] = new HttpOp[F]()
}

trait HttpInterpret[F[_]] extends ~>[HttpADT, F] {
  def get(query: HttpQuery): F[Bytes]
  def create(query: HttpQuery, payload: Bytes): F[Bytes]
  def update(query: HttpQuery, payload: Bytes): F[Bytes]
  def delete(query: HttpQuery): F[Bytes]

  override def apply[A](fa: HttpADT[A]): F[A] = fa match {
    case Get(query) => get(query)
    case Put(query, payload) => update(query, payload)
    case Post(query, payload) => create(query, payload)
    case Delete(query) => delete(query)
  }
}

sealed trait Credentials
case class AccessKey(key: String) extends Credentials
case class OAuth2Token(token: String) extends Credentials

class AkkaHttpInterpret(credentials: Credentials)
                       (implicit actorSystem: ActorSystem, mat: Materializer,
                        exc: ExecutionContext) extends HttpInterpret[Future] {

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
          uri = Uri(query.url).withQuery(Query(query.params + ("apiKey" -> key)))
        )
      case OAuth2Token(token) =>
        HttpRequest(
          uri = Uri(query.url).withQuery(Query(query.params))
        ).withHeaders(headers.Authorization(OAuth2BearerToken(token)))
    }

  private def createRequest(method: HttpMethod, query: HttpQuery, payload: Bytes): HttpRequest =
    createRequest(method, query).withEntity(payload)

  private def doRequest(request: HttpRequest): Future[Bytes] =
    for {
      response <- http.singleRequest(setupCredentials(request, credentials))
      data <- response.entity.toStrict(timeout).map(_.data.utf8String)
      _ = if (response.status.isFailure()) {
        response.entity.discardBytes()
        throw BacklogApiException(request.uri.toString(), response.status.intValue(), data)
      }
    } yield data

  override def create(query: HttpQuery, payload: Bytes): Future[Bytes] =
    doRequest(createRequest(HttpMethods.POST, query, payload))

  override def get(query: HttpQuery): Future[Bytes] =
    doRequest(createRequest(HttpMethods.GET, query))

  override def update(query: HttpQuery, payload: Bytes): Future[Bytes] =
    doRequest(createRequest(HttpMethods.PUT, query, payload))

  override def delete(query: HttpQuery): Future[Bytes] = {
    doRequest(createRequest(HttpMethods.DELETE, query))
  }
}
