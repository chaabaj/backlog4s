package com.nulabinc.backlog4s.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.stream.Materializer
import cats.free.Free
import cats.{InjectK, ~>}
import com.nulabinc.backlog4s.datas.ApiErrors
import com.nulabinc.backlog4s.dsl.HttpADT.{Bytes, Response}
import spray.json.JsonFormat
import cats.implicits._
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

sealed trait HttpError
case class RequestError(errors: ApiErrors) extends HttpError
case object ServerDown extends HttpError

object HttpADT {
  type Bytes = String
  type Response[A] = Either[HttpError, A]
}

sealed trait HttpADT[A]
case class Get[A](query: HttpQuery, format: JsonFormat[A])
  extends HttpADT[Response[A]]
case class Post[A](query: HttpQuery, payload: A, format: JsonFormat[A])
  extends HttpADT[Response[A]]
case class Put[A](query: HttpQuery, payload: A, format: JsonFormat[A])
  extends HttpADT[Response[A]]
case class Delete(query: HttpQuery) extends HttpADT[Response[Unit]]

class BacklogHttpOp[F[_]](implicit I: InjectK[HttpADT, F]) {

  type HttpF[A] = Free[F, A]

  def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): HttpF[Response[A]] =
    Free.inject[HttpADT, F](Get(query, format))

  def post[A](query: HttpQuery, payload: A)(implicit format: JsonFormat[A]): HttpF[Response[A]] =
    Free.inject[HttpADT, F](Post(query, payload, format))

  def put[A](query: HttpQuery, payload: A)(implicit format: JsonFormat[A]): HttpF[Response[A]] =
    Free.inject[HttpADT, F](Put(query, payload, format))

  def delete(query: HttpQuery): HttpF[Response[Unit]] =
    Free.inject[HttpADT, F](Delete(query))
}

object BacklogHttpOp {
  implicit def httpOp[F[_]](implicit I: InjectK[HttpADT, F]): BacklogHttpOp[F] =
    new BacklogHttpOp[F]()
}

trait BacklogHttpInterpret[F[_]] extends ~>[HttpADT, F] {
  def get[A](query: HttpQuery, format: JsonFormat[A]): F[Response[A]]
  def create[A](query: HttpQuery, payload: A,
                format: JsonFormat[A]): F[Response[A]]
  def update[A](query: HttpQuery, payload: A,
                format: JsonFormat[A]): F[Response[A]]
  def delete(query: HttpQuery): F[Response[Unit]]

  override def apply[A](fa: HttpADT[A]): F[A] = fa match {
    case Get(query, format) => get(query, format)
    case Put(query, payload, format) => update(query, payload, format)
    case Post(query, payload, format) => create(query, payload, format)
    case Delete(query) => delete(query)
  }
}

sealed trait Credentials
case class AccessKey(key: String) extends Credentials
case class OAuth2Token(token: String) extends Credentials

class AkkaHttpInterpret(baseUrl: String, credentials: Credentials)
                       (implicit actorSystem: ActorSystem, mat: Materializer,
                        exc: ExecutionContext) extends BacklogHttpInterpret[Future] {


  import com.nulabinc.backlog4s.formatters.SprayJsonFormats._

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
          uri = Uri(baseUrl + query.url).withQuery(Query(query.params + ("apiKey" -> key)))
        )
      case OAuth2Token(token) =>
        HttpRequest(
          uri = Uri(baseUrl + query.url).withQuery(Query(query.params))
        ).withHeaders(headers.Authorization(OAuth2BearerToken(token)))
    }

  private def createRequest[A](method: HttpMethod, query: HttpQuery,
                               payload: A, format: JsonFormat[A]): HttpRequest =
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

  override def create[A](query: HttpQuery, payload: A,
                         format: JsonFormat[A]): Future[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.POST, query, payload, format))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response


  override def get[A](query: HttpQuery, format: JsonFormat[A]): Future[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.GET, query))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  override def update[A](query: HttpQuery, payload: A,
                         format: JsonFormat[A]): Future[Response[A]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.PUT, query, payload, format))
      response = serverResponse.map(_.parseJson.convertTo[A](format))
    } yield response

  override def delete(query: HttpQuery): Future[Response[Unit]] =
    for {
      serverResponse <- doRequest(createRequest(HttpMethods.DELETE, query))
      response = serverResponse.map(_ => ())
    } yield response

}
