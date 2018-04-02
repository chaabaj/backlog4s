package com.github.backlog4s.interpreters

import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.Charset

import com.github.backlog4s.datas.{AccessKey, ApiErrors, Credentials, OAuth2Token}
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.HttpADT.{ByteStream, Response}
import com.github.backlog4s.dsl._
import cats.effect.IO
import hammock.jvm.Interpreter
import hammock._
import cats.implicits._
import spray.json._
import hammock.hi._
import com.github.backlog4s.formatters.SprayJsonFormats._
import cats.Monad
import hammock.Entity.{ByteArrayEntity, StringEntity}
import monix.reactive.Observable

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object HammockInterpreter {
  // hmmm... no way to pass directly a entity to hammock
  // need to define a codec for entity(just doing nothing lol)
  implicit object EntityCodec extends Codec[Entity] {
    override def encode(a: Entity): Entity = a

    override def decode(a: Entity): Either[CodecException, Entity] =
      Either.right(a.cata(e => e,  e => e))
  }
}

class HammockInterpreter()(implicit val hammockInterpreter: Interpreter[IO],
                           override val exc: ExecutionContext)
  extends BacklogHttpInterpret[Future] with WithFutureCompletion {

  implicit val monad = implicitly[Monad[Future]]

  import HammockInterpreter._

  private val reqHeaders =
    header("User-Agent" -> "backlog4s") >>>
    header("Accept-Charset" -> "UTF-8")

  private val utf8 = Charset.forName("UTF-8")

  private def createRequest[A](method: Method,
                               query: HttpQuery,
                               body: Option[Entity] = None,
                               opts: Opts = Opts.empty) = {
    val uri = Uri.unsafeParse(query.baseUrl + query.path)
      .copy(query = QueryParam.encodeAll(query.params))

    val contentType = body.map {
      case _: StringEntity => header("Content-Type" -> "application/json")
      case _: ByteArrayEntity => header("Content-Type" -> "application/octet-stream")
    }.getOrElse(header("Content-Type" -> "application/json"))

    query.credentials match {
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

      content.parseJson.convertTo[A](format)
    }
  }

  private def discardBody(response: Response[Entity]): Response[Unit] =
    response.map(_ => ())

  private def asByteStream(response: Response[Entity]): Response[ByteStream] =
    response.flatMap {
      case ByteArrayEntity(bytes, _) =>
        Either.right(
          Observable.eval(ByteBuffer.wrap(bytes))
        )
      case StringEntity(content, contentType) =>
        Either.right(
          Observable.eval(
            ByteBuffer.wrap(content.getBytes(utf8))
          )
        )
    }

  private def jsonEntity[Payload](payload: Payload, format: JsonFormat[Payload]): StringEntity =
    StringEntity(payload.toJson(format).compactPrint)

  override def pure[A](a: A): Future[A] = Future.successful(a)

  override def parallel[A](prgs: Seq[BacklogHttpOp.HttpF[A]]): Future[Seq[A]] =
    Future.sequence(
      prgs.map(_.foldMap(this))
    )

  override def get[A](query: HttpQuery, format: JsonFormat[A]): Future[Response[A]] =
    createRequest(Method.GET, query)
      .map(handleResponse)
      .map(jsonResponseAs[A](_, format))
      .exec[IO].unsafeToFuture()


  override def create[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): Future[Response[A]] = {
    createRequest(Method.POST, query, Some(jsonEntity(payload, payloadFormat)))
      .map(handleResponse)
      .map(jsonResponseAs[A](_, format))
      .exec[IO].unsafeToFuture()
  }


  override def delete(query: HttpQuery): Future[Response[Unit]] =
    createRequest(Method.DELETE, query)
      .map(handleResponse)
      .map(discardBody)
      .exec[IO].unsafeToFuture()

  override def update[Payload, A](query: HttpQuery,
                                  payload: Payload,
                                  format: JsonFormat[A],
                                  payloadFormat: JsonFormat[Payload]): Future[Response[A]] =
    createRequest(Method.PUT, query, Some(jsonEntity(payload, payloadFormat)))
      .map(handleResponse)
      .map(jsonResponseAs[A](_, format))
      .exec[IO].unsafeToFuture()

  override def download(query: HttpQuery): Future[Response[ByteStream]] =
    createRequest(Method.GET, query)
      .map(handleResponse)
      .map(asByteStream)
      .exec[IO].unsafeToFuture()

  // not supported yet need to figure out how to upload a file using hammock
  override def upload[A](query: HttpQuery,
                         file: File,
                         format: JsonFormat[A]): Future[Response[A]] = ???
}
