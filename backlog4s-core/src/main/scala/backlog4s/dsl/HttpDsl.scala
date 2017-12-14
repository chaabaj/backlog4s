package backlog4s.dsl


import java.nio.ByteBuffer

import cats.effect.IO
import cats.free.Free
import cats.{InjectK, ~>}
import backlog4s.datas.ApiErrors
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import spray.json.JsonFormat
import fs2.Stream

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
case class Pure[A](a: A) extends HttpADT[A]


class BacklogHttpOp[F[_]](implicit I: InjectK[HttpADT, F]) {

  type HttpF[A] = Free[F, A]

  def pure[A](a: A): HttpF[A] =
    Free.inject[HttpADT, F](Pure(a))

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

trait BacklogHttpInterpret[F[_]] extends (HttpADT ~> F) {
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
  def pure[A](a: A): F[A]

  override def apply[A](fa: HttpADT[A]): F[A] = fa match {
    case Pure(a) => pure(a)
    case Get(query, format) =>
      get(query, format)
    case Put(query, payload, format, payloadFormat) =>
      update(query, payload, format, payloadFormat)
    case Post(query, payload, format, payloadFormat) =>
      create(query, payload, format, payloadFormat)
    case Delete(query) =>
      delete(query)
    case Download(query) =>
      download(query)
  }
}
