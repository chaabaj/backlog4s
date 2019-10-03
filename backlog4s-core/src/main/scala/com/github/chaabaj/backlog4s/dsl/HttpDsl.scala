package com.github.chaabaj.backlog4s.dsl

import java.io.File
import java.nio.ByteBuffer

import com.github.chaabaj.backlog4s.datas.ApiErrors
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}
import monix.reactive.Observable
import spray.json.JsonFormat

trait BacklogHttpDsl[F[_]] {
  def get[A](query: HttpQuery)(implicit format: JsonFormat[A]): F[Response[A]]
  def post[Payload, A](query: HttpQuery, payload: Payload)(implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): F[Response[A]]
  def put[Payload, A](query: HttpQuery, payload: Payload)(implicit format: JsonFormat[A], payloadFormat: JsonFormat[Payload]): F[Response[A]]
  def delete(query: HttpQuery): F[Response[Unit]]
  def download(query: HttpQuery): F[Response[ByteStream]]
  def upload[A](query: HttpQuery, file: File)(implicit format: JsonFormat[A]): F[Response[A]]
}

object BacklogHttpDsl {
  type Bytes = ByteBuffer
  type Response[A] = Either[HttpError, A]
  type ByteStream = Observable[Bytes]
}

sealed trait HttpError
case class RequestError(errors: ApiErrors) extends HttpError
case class InvalidResponse(msg: String) extends HttpError
case object ServerDown extends HttpError
