package backlog4s.interpreters

import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.Charset

import backlog4s.datas.{AccessKey, ApiErrors, Credentials, OAuth2Token}
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl._
import cats.effect.IO
import hammock.jvm.Interpreter
import hammock._
import cats.implicits._
import spray.json._
import hammock.hi._
import backlog4s.formatters.SprayJsonFormats._
import cats.Monad
import hammock.Entity.{ByteArrayEntity, StringEntity}
import fs2.Stream

import scala.concurrent.Future

object HammockInterpreter {
  // hmmm... no way to pass directly a entity to hammock
  // need to define a codec for entity(just doing nothing lol)
  implicit object EntityCodec extends Codec[Entity] {
    override def encode(a: Entity): Entity = a

    override def decode(a: Entity): Either[CodecException, Entity] =
      Either.right(a.cata(e => e,  e => e))
  }
}

class HammockInterpreter(baseUrl: String, credentials: Credentials)
                        (implicit val hammockInterpreter: Interpreter[IO])
  extends BacklogHttpInterpret[IO] {

  implicit val monad = implicitly[Monad[IO]]

  import HammockInterpreter._

  private val reqHeaders =
    header("User-Agent" -> "backlog4s") >>>
    header("Accept-Charset" -> "UTF-8")

  private val utf8 = Charset.forName("UTF-8")

  private def createRequest[A](method: Method,
                               query: HttpQuery,
                               body: Option[Entity] = None,
                               opts: Opts = Opts.empty) = {
    val uri = Uri.unsafeParse(baseUrl + query.path)
      .copy(query = query.params)

    val contentType = body.map {
      case _: StringEntity => header("Content-Type" -> "application/json")
      case _: ByteArrayEntity => header("Content-Type" -> "application/octet-stream")
    }.getOrElse(header("Content-Type" -> "application/json"))

    credentials match {
      case AccessKey(key) =>
        Hammock.withOpts(
          method,
          uri.copy(query = uri.query + ("apiKey" -> key)),
          (reqHeaders >>> contentType)(opts),
          body
        )
      case OAuth2Token(token) =>
        Hammock.withOpts(
          method,
          uri,
          (
            reqHeaders >>>
              auth(Auth.OAuth2Bearer(token)) >>>
              contentType
          )(opts),
          body
        )
    }
  }

  private def handleResponse(response: HttpResponse): Response[Entity] = {
    val status = response.status.code

    if (status >= 400 && status < 500) {
      val content = response.entity.cata(_.body, _.body.toString)
      val apiErrors = content.parseJson.convertTo[ApiErrors]
      Either.left(RequestError(apiErrors))
    } else if (status >= 500) {
      Either.left(ServerDown)
    } else {
      Either.right(response.entity)
    }
  }

  private def jsonResponseAs[A](response: Response[Entity],
                                format: JsonFormat[A]): Response[A] = {
    response.map { entity =>
      val content = entity.cata(_.body, _.body.toString)

      println(s"received $content")
      content.parseJson.convertTo[A](format)
    }
  }

  private def discardBody(response: Response[Entity]): Response[Unit] =
    response.map(_ => ())

  private def asByteStream(response: Response[Entity]): Response[ByteStream] =
    response.flatMap {
      case ByteArrayEntity(bytes, _) =>
        Either.right(Stream.eval(IO.pure(ByteBuffer.wrap(bytes))))
      case StringEntity(content, contentType) =>
        println(contentType.name)
        Either.right(
          Stream.eval(
            IO.pure(
              ByteBuffer.wrap(content.getBytes(utf8))
            )
          )
        )
    }

  private def jsonEntity[Payload](payload: Payload, format: JsonFormat[Payload]): StringEntity =
    StringEntity(payload.toJson(format).compactPrint)



  override def pure[A](a: A): IO[A] = IO.apply(a)

  override def parallel[A](prgs: Seq[BacklogHttpOp.HttpF[A]]): IO[Seq[A]] = ???

  override def get[A](query: HttpQuery, format: JsonFormat[A]): IO[Response[A]] =
    createRequest(Method.GET, query)
      .map(handleResponse)
      .map(jsonResponseAs[A](_, format))
      .exec[IO]


  override def create[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): IO[Response[A]] = {
    createRequest(Method.POST, query, Some(jsonEntity(payload, payloadFormat)))
      .map(handleResponse)
      .map(jsonResponseAs[A](_, format))
      .exec[IO]
  }


  override def delete(query: HttpQuery): IO[Response[Unit]] =
    createRequest(Method.DELETE, query)
      .map(handleResponse)
      .map(discardBody)
      .exec[IO]

  override def update[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): IO[Response[A]] =
    createRequest(Method.PUT, query, Some(jsonEntity(payload, payloadFormat)))
      .map(handleResponse)
      .map(jsonResponseAs[A](_, format))
      .exec[IO]

  override def download(query: HttpQuery): IO[Response[ByteStream]] =
    createRequest(Method.GET, query)
      .map(handleResponse)
      .map(asByteStream)
      .exec[IO]

  // not supported yet need to figure out how to upload a file using hammock
  override def upload[A](query: HttpQuery,
                         file: File,
                         format: JsonFormat[A]): IO[Response[A]] = ???
}
