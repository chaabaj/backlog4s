package backlog4s.dsl

import java.nio.ByteBuffer
import java.io.File

import cats.effect.IO
import cats.free.Free
import cats.{InjectK, Monad, ~>}
import backlog4s.datas.{ApiErrors, Credentials}
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import spray.json.JsonFormat
import fs2.Stream

sealed trait HttpError
case class RequestError(errors: ApiErrors) extends HttpError
case class InvalidResponse(msg: String) extends HttpError
case object ServerDown extends HttpError

object HttpADT {
  type Bytes = String
  type Response[A] = Either[HttpError, A]
  type ByteStream = Stream[IO, ByteBuffer]
}

sealed trait HttpADT[A]
private[dsl] case class Get[A](query: HttpQuery,
                  format: JsonFormat[A])
  extends HttpADT[Response[A]]
private[dsl] case class Post[Payload, A](
  query: HttpQuery,
  payload: Payload,
  format: JsonFormat[A],
  payloadFormat: JsonFormat[Payload]
) extends HttpADT[Response[A]]
private[dsl] case class Put[Payload, A](
  query: HttpQuery,
  payload: Payload,
  format: JsonFormat[A],
  payloadFormat: JsonFormat[Payload]
) extends HttpADT[Response[A]]
private[dsl] case class Delete(query: HttpQuery) extends HttpADT[Response[Unit]]
private[dsl] case class Download(query: HttpQuery)
  extends HttpADT[Response[ByteStream]]
private[dsl] case class Upload[A](query: HttpQuery,
                     file: File,
                     format: JsonFormat[A])
  extends HttpADT[Response[A]]
private[dsl] case class Pure[A](a: A) extends HttpADT[A]
private[dsl] case class Suspend[A](a: () => Free[HttpADT, A]) extends HttpADT[A]

object BacklogHttpOp {
  type HttpF[A] = Free[HttpADT, A]

  def pure[A](a: A): HttpF[A] =
    Free.liftF(Pure(a))

  def suspend[A](a: => HttpF[A]): HttpF[A] =
    Free.liftF(Suspend(() => a))

  def post[Payload, A](query: HttpQuery, payload: Payload)
                      (implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): HttpF[Response[A]] =
    Free.liftF(Post(query, payload, format, payloadFormat))

  def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): HttpF[Response[A]] =
    Free.liftF[HttpADT, Response[A]](Get(query, format))

  def put[Payload, A](query: HttpQuery, payload: Payload)
                     (implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): HttpF[Response[A]] =
    Free.liftF(Put(query, payload, format, payloadFormat))

  def delete(query: HttpQuery): HttpF[Response[Unit]] =
    Free.liftF(Delete(query))

  def download(query: HttpQuery): HttpF[Response[ByteStream]] =
    Free.liftF(Download(query))

  def upload[A](query: HttpQuery, file: File)(implicit format: JsonFormat[A]): HttpF[Response[A]] =
    Free.liftF[HttpADT, Response[A]](Upload(query, file, format))
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
  def upload[A](query: HttpQuery, file: File, format: JsonFormat[A]): F[Response[A]]
  def pure[A](a: A): F[A]

  implicit def monad: Monad[F]

  override def apply[A](fa: HttpADT[A]): F[A] = fa match {
    case Pure(a) => pure(a)
    case Suspend(prg) => prg().foldMap(this)
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
    case Upload(query, file, format) =>
      upload(query, file, format)
  }
}
