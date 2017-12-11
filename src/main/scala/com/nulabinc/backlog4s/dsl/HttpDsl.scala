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
case class Token(token: String) extends Credentials

class AkkaHttpInterpret(baseUrl: String, credentials: AccessKey)
                       (implicit actorSystem: ActorSystem, mat: Materializer,
                        exc: ExecutionContext) extends HttpInterpret[Future] {

  private val http = Http()
  private val timeout = 10.seconds

  private def doRequest(request: HttpRequest): Future[Bytes] =
    for {
      response <- http.singleRequest(request)
      _ = if (response.status.isFailure()) {
        response.entity.discardBytes()
        throw new RuntimeException(s"Failed request for query ${request.uri} with status ${response.status}")
      }
      data <- response.entity.toStrict(timeout).map(_.data.utf8String)
    } yield data

  override def create(query: HttpQuery, payload: Bytes): Future[Bytes] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = Uri(baseUrl + query.url)
        .withQuery(Query(query.params + ("apiKey" -> credentials.key)))
    ).withEntity(payload)

    doRequest(request)
  }

  override def get(query: HttpQuery): Future[Bytes] = {
    val request = HttpRequest(
      uri = Uri(baseUrl + query.url)
        .withQuery(Query(query.params + ("apiKey" -> credentials.key)))
    )

    doRequest(request)
  }

  override def update(query: HttpQuery, payload: Bytes): Future[Bytes] = {
    val request = HttpRequest(
      method = HttpMethods.PUT,
      uri = Uri(baseUrl + query.url)
        .withQuery(Query(query.params + ("apiKey" -> credentials.key)))
    ).withEntity(payload)

    doRequest(request)
  }

  override def delete(query: HttpQuery): Future[Bytes] = {
    val request = HttpRequest(
      method = HttpMethods.DELETE,
      uri = Uri(baseUrl + query.url)
        .withQuery(Query(query.params + ("apiKey" -> credentials.key)))
    )

    doRequest(request)
  }
}
