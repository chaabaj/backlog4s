package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class ProjectApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  private val resource = "projects"

  def all(offset: Int = 0, count: Int = 100,
          archived: Option[Boolean] = None,
          all: Boolean = false): F[Response[Seq[Project]]] = {
    val params = Seq(
      QueryParam("offset", offset),
      QueryParam("count", count),
      QueryParam.option("archived", archived),
      QueryParam("all", all)
    )

    BacklogHttpDsl.get[Seq[Project]](HttpQuery(resource, params, credentials, baseUrl))
  }

  def byIdOrKey(idOrKey: IdOrKeyParam[Project]): F[Response[Project]] =
    BacklogHttpDsl.get[Project](
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def admins(idOrKey: IdOrKeyParam[Project]): F[Response[Seq[User]]] =
    BacklogHttpDsl.get[Seq[User]](
      HttpQuery(
        path = s"$resource/$idOrKey/administrators",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def users(idOrKey: IdOrKeyParam[Project]): F[Response[Seq[User]]] =
    BacklogHttpDsl.get[Seq[User]](
      HttpQuery(
        path = s"$resource/$idOrKey/users",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def icon(idOrKey: IdOrKeyParam[Project]): F[Response[ByteStream]] =
    BacklogHttpDsl.download(
      HttpQuery(
        path = s"$resource/$idOrKey/image",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def recentlyViewed(order: Order = Order.Desc): F[Response[Seq[Project]]] =
    BacklogHttpDsl.get[Seq[Project]](
      HttpQuery(
        path = "users/myself/recentlyViewedProjects",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def create(addProjectForm: AddProjectForm): F[Response[Project]] =
    BacklogHttpDsl.post[AddProjectForm, Project](
      HttpQuery(path = resource, credentials = credentials, baseUrl = baseUrl),
      addProjectForm
    )

  def addAdmin(idOrKey: IdOrKeyParam[Project], userId: Id[User]): F[Response[User]] =
    BacklogHttpDsl.post[CustomForm, User](
      HttpQuery(
        path = s"$resource/$idOrKey/administrators",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "userId" -> userId.value.toString
      )
    )

  def removeAdmin(idOrKey: IdOrKeyParam[Project], userId: Id[User]): F[Response[Unit]] =
    BacklogHttpDsl.delete(HttpQuery(
      path = s"$resource/$idOrKey/administrators",
      params = QueryParam.single("userId", userId),
      credentials = credentials,
      baseUrl = baseUrl
    ))

  def addUser(idOrKey: IdOrKeyParam[Project], userId: Id[User]): F[Response[User]] =
    BacklogHttpDsl.post[CustomForm, User](
      HttpQuery(
        path = s"$resource/$idOrKey/users",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "userId" -> userId.toString
      )
    )

  def removeUser(idOrKey: IdOrKeyParam[Project], userId: Id[User]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/$idOrKey/users",
        params = QueryParam.single("userId", userId),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def update(idOrKey: IdOrKeyParam[Project], form: UpdateProjectForm): F[Response[Project]] =
    BacklogHttpDsl.put[UpdateProjectForm, Project](
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(idOrKey: IdOrKeyParam[Project]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
