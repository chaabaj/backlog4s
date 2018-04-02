package com.github.backlog4s.dsl

import java.nio.ByteBuffer
import java.io.File

import cats.free.Free
import cats.{Monad, ~>}
import com.github.backlog4s.datas.ApiErrors
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.BacklogHttpOp.HttpF
import com.github.backlog4s.dsl.HttpADT.{ByteStream, Response}
import com.github.backlog4s.streaming.ApiStream.ApiStream
import spray.json.JsonFormat
import monix.reactive.Observable
import com.github.backlog4s.streaming.StreamingEffect._

import scala.util.Try

sealed trait HttpError
case class RequestError(errors: ApiErrors) extends HttpError
case class InvalidResponse(msg: String) extends HttpError
case object ServerDown extends HttpError

object HttpADT {
  type Bytes = ByteBuffer
  type Response[A] = Either[HttpError, A]
  type ByteStream = Observable[Bytes]
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
private[dsl] case class Parallel[A](prgs: Seq[Free[HttpADT, A]]) extends HttpADT[Seq[A]]

object BacklogHttpOp {
  type HttpF[A] = Free[HttpADT, A]

  def pure[A](a: A): HttpF[A] =
    Free.liftF(Pure(a))

  def suspend[A](a: => HttpF[A]): HttpF[A] =
    Free.liftF(Suspend(() => a))

  def parallel[A](prgs: Seq[HttpF[A]]): HttpF[Seq[A]] =
    Free.liftF[HttpADT, Seq[A]](Parallel(prgs))

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

trait BacklogHttpInterpret[F[_]] extends (HttpADT ~> F) with WithCompletion[F] {
  def get[A](query: HttpQuery, format: JsonFormat [A]): F[Response[A]]
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
  def parallel[A](prgs: Seq[HttpF[A]]): F[Seq[A]]

  def run[A](apiPrg: ApiPrg[A]): F[A] = apiPrg.foldMap(this)
  def runStream[A](stream: ApiStream[A]): F[Unit] =
    stream.compile.drain.foldMap(this)
  def runStreamWithResult[A](stream: ApiStream[A]): F[Vector[Seq[A]]] =
    stream.compile.toVector.foldMap(this)

  implicit def monad: Monad[F]

  override def apply[A](fa: HttpADT[A]): F[A] = fa match {
    case Pure(a) => pure(a)
    case Suspend(prg) => prg().foldMap(this)
    case Parallel(prgs) => parallel(prgs)
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
