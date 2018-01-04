package backlog4s.interpreters

import java.io.File

import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.{BacklogHttpInterpret, HttpQuery, ServerDown}
import cats.effect.IO
import hammock.jvm.Interpreter
import hammock._
import hammock.marshalling._
import spray.json._

sealed trait Credentials
case class AccessKey(key: String) extends Credentials
case class OAuth2Token(token: String) extends Credentials

object TestHammock {
  implicit val interpreter = Interpreter[IO]

  val response = Hammock
    .request(Method.GET, Uri.unsafeParse("https:://www.google.fr"), Map())
    .as[String]
    .exec[IO]
}


class HammockInterpreter(baseUrl: String, credentials: Credentials)
  extends BacklogHttpInterpret[IO] {

  implicit val interpreter = Interpreter[IO]

  private val reqHeaders = Map(
    "User-Agent" -> "backlog4s",
    "Content-Type" -> "application/json",
    "Accept-Charset" -> "UTF-8"
  )

  private def createRequest(method: Method, query: HttpQuery) = {
    val uri = Uri.unsafeParse(baseUrl + query.path)
      .copy(query = query.params)

    credentials match {
      case AccessKey(key) =>
        Hammock.request(method, uri.copy(
          query = uri.query + ("apiKey" -> key)),
          reqHeaders
        )
      case OAuth2Token(token) => ???
    }
  }


  override def get[A](query: HttpQuery, format: JsonFormat[A]): IO[Response[A]] =
    createRequest(Method.GET, query)
      .as[String]
      .exec[IO]
      .attempt
      .map {
        case Left(throwable) => Left(ServerDown)
        case Right(data) => Right(data.parseJson.convertTo[A](format))
      }

  override def create[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): IO[Response[A]] = ???

  override def delete(query: HttpQuery): IO[Response[Unit]] = ???

  override def update[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): IO[Response[A]] = ???

  override def download(query: HttpQuery): IO[Response[ByteStream]] = ???

  override def upload[A](query: HttpQuery,
                         file: File,
                         format: JsonFormat[A]): IO[Response[A]] = ???
}
