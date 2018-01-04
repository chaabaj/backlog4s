package backlog4s.interpreters

import java.io.File

import backlog4s.datas.{AccessKey, ApiErrors, Credentials, OAuth2Token}
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.{BacklogHttpInterpret, HttpQuery, RequestError, ServerDown}
import cats.effect.IO
import hammock.jvm.Interpreter
import hammock._
import cats.implicits._
import spray.json._
import hammock.hi._
import backlog4s.formatters.SprayJsonFormats._
import hammock.Entity.StringEntity

object HammockInterpreter {
  implicit object StringCodec extends Codec[String] {
    override def encode(a: String): Entity = StringEntity(a)

    override def decode(a: Entity): Either[CodecException, String] =
      Either.right(a.cata(_.body, _.body.toString))
  }
}

class HammockInterpreter(baseUrl: String, credentials: Credentials)
                        (implicit val hammockInterpreter: Interpreter[IO])
  extends BacklogHttpInterpret[IO] {

  import HammockInterpreter._

  private val reqHeaders =
    header("User-Agent" -> "backlog4s") >>>
    header("Content-Type" -> "application/json") >>>
    header("Accept-Charset" -> "UTF-8")

  private def createRequest[A](method: Method,
                               query: HttpQuery,
                               body: Option[String] = None) = {
    val uri = Uri.unsafeParse(baseUrl + query.path)
      .copy(query = query.params)

    credentials match {
      case AccessKey(key) =>
        Hammock.withOpts(
          method,
          uri.copy(query = uri.query + ("apiKey" -> key)),
          reqHeaders(Opts.empty),
          body
        )
      case OAuth2Token(token) =>
        Hammock.withOpts(
          method,
          uri,
          (reqHeaders >>> auth(Auth.OAuth2Bearer(token)))(Opts.empty),
          body
        )
    }
  }

  private def parseResponse(response: HttpResponse): Response[String] = {
    val content = response.entity.cata(_.body, _.body.toString)
    val status = response.status.code

    if (status >= 400 && status < 500) {
      val apiErrors = content.parseJson.convertTo[ApiErrors]
      Either.left(RequestError(apiErrors))
    } else if (status >= 500) {
      Either.left(ServerDown)
    } else {
      println(s"received $content")
      Either.right(content)
    }
  }

  override def pure[A](a: A): IO[A] = IO.apply(a)

  override def get[A](query: HttpQuery, format: JsonFormat[A]): IO[Response[A]] =
    createRequest(Method.GET, query)
      .map(parseResponse)
      .map { response =>
        response.map(_.parseJson.convertTo[A](format))
      }
      .exec[IO]

  override def create[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): IO[Response[A]] =
    createRequest(Method.POST, query, Some(payload.toJson(payloadFormat).compactPrint))
      .map(parseResponse)
      .map { response =>
        response.map(_.parseJson.convertTo[A](format))
      }
      .exec[IO]

  override def delete(query: HttpQuery): IO[Response[Unit]] =
    createRequest(Method.DELETE, query)
      .map(parseResponse)
      .map { response =>
        response.map(_ => ())
      }
      .exec[IO]

  override def update[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): IO[Response[A]] =
    createRequest(Method.PUT, query, Some(payload.toJson(payloadFormat).compactPrint))
      .map(parseResponse)
      .map { response =>
        response.map(_.parseJson.convertTo[A](format))
      }
      .exec[IO]

  override def download(query: HttpQuery): IO[Response[ByteStream]] = ???

  override def upload[A](query: HttpQuery,
                         file: File,
                         format: JsonFormat[A]): IO[Response[A]] = ???
}
