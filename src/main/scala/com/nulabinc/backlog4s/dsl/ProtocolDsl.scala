package com.nulabinc.backlog4s.dsl

import java.nio.charset.Charset

import cats.free.Free
import cats.{InjectK, ~>}
import com.nulabinc.backlog4s.dsl.HttpADT.Bytes
import spray.json.JsonFormat

import scala.concurrent.Future

trait Protocol[Format[_]] {
  sealed trait ProtocolADT[A]
  case class Decode[A](bytes: Bytes, format: Format[A]) extends ProtocolADT[A]
  case class Encode[A](a: A, format: Format[A]) extends ProtocolADT[Bytes]

  class ProtocolOp[F[_]](implicit I: InjectK[ProtocolADT, F]) {

    def encode[A](a: A)(implicit format: Format[A]): Free[F, Bytes] =
      Free.inject[ProtocolADT, F](Encode[A](a, format))

    def decode[A](bytes: Bytes)(implicit format: Format[A]): Free[F, A]=
      Free.inject[ProtocolADT, F](Decode(bytes, format))
  }

  trait Interpret[F[_]] extends ~>[ProtocolADT, F] {
    def encode[A](a: A, format: Format[A]): F[Bytes]
    def decode[A](bytes: Bytes, format: Format[A]): F[A]

    override def apply[A](fa: ProtocolADT[A]): F[A] = fa match {
      case Encode(obj, format) => encode(obj, format)
      case Decode(bytes, format) => decode(bytes, format.asInstanceOf[Format[A]])
    }
  }

  object ProtocolOp {
    implicit def ProtocolOp[F[_]](implicit I: InjectK[ProtocolADT, F]): ProtocolOp[F] = new ProtocolOp[F]()
  }

}

object JsonProtocol extends Protocol[JsonFormat]

object SprayJsonProtocolDsl extends JsonProtocol.Interpret[Future] {
  import spray.json._

  override def encode[A](a: A, format: JsonFormat[A]): Future[Bytes] =
    Future.successful(a.toJson(format).compactPrint)

  override def decode[A](bytes: Bytes, format: JsonFormat[A]): Future[A] =
    Future.successful(bytes.toString.parseJson.convertTo[A](format))
}