package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class UserApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  private val resource = "users"


  // stream[A](() => ApiPrg[Response[Seq[A]]): Stream[IO, Seq[A]]
  lazy val all: F[Response[Seq[User]]] =
    BacklogHttpDsl.get[Seq[User]](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def byId(id: Id[User]): F[Response[User]] = {
    val query = HttpQuery(
      path = s"$resource/myself",
      credentials = credentials,
      baseUrl = baseUrl
    )
    if (id == UserT.myself)
      BacklogHttpDsl.get[User](query)
    else
      BacklogHttpDsl.get[User](query.copy(path = s"$resource/${id.value}"))
  }


  def create(form: AddUserForm): F[Response[User]] =
    BacklogHttpDsl.post[AddUserForm, User](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(id: Id[User], form: UpdateUserForm): F[Response[User]] =
    BacklogHttpDsl.put[UpdateUserForm, User](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(id: Id[User]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadIcon(id: Id[User]): F[Response[ByteStream]] =
    BacklogHttpDsl.download(
      HttpQuery(
        path = s"$resource/${id.value}/icon",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
