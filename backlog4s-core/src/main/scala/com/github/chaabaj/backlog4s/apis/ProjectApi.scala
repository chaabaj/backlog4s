package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.{ByteStream, Response}
import com.github.chaabaj.backlog4s.dsl.{HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class ProjectApi(override val baseUrl: String,
                 override val credentials: Credentials) extends Api {
  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "projects"

  def all(offset: Int = 0, count: Int = 100,
          archived: Option[Boolean] = None,
          all: Boolean = false): ApiPrg[Response[Seq[Project]]] = {
    val params = Seq(
      QueryParam("offset", offset),
      QueryParam("count", count),
      QueryParam.option("archived", archived),
      QueryParam("all", all)
    )

    get[Seq[Project]](HttpQuery(resource, params, credentials, baseUrl))
  }

  def byIdOrKey(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Project]] =
    get[Project](
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def admins(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](
      HttpQuery(
        path = s"$resource/$idOrKey/administrators",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def users(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](
      HttpQuery(
        path = s"$resource/$idOrKey/users",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def icon(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        path = s"$resource/$idOrKey/image",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def recentlyViewed(order: Order = Order.Desc): ApiPrg[Response[Seq[Project]]] =
    get[Seq[Project]](
      HttpQuery(
        path = "users/myself/recentlyViewedProjects",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def create(addProjectForm: AddProjectForm): ApiPrg[Response[Project]] =
    post[AddProjectForm, Project](
      HttpQuery(path = resource, credentials = credentials, baseUrl = baseUrl),
      addProjectForm
    )

  def addAdmin(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[User]] =
    post[CustomForm, User](
      HttpQuery(
        path = s"$resource/$idOrKey/administrators",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "userId" -> userId.value.toString
      )
    )

  def removeAdmin(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    delete(HttpQuery(
      path = s"$resource/$idOrKey/administrators",
      params = QueryParam.single("userId", userId),
      credentials = credentials,
      baseUrl = baseUrl
    ))

  def addUser(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[User]] =
    post[CustomForm, User](
      HttpQuery(
        path = s"$resource/$idOrKey/users",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "userId" -> userId.toString
      )
    )

  def removeUser(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/$idOrKey/users",
        params = QueryParam.single("userId", userId),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def update(idOrKey: IdOrKeyParam[Project], form: UpdateProjectForm): ApiPrg[Response[Project]] =
    put[UpdateProjectForm, Project](
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object ProjectApi extends ApiContext[ProjectApi] {
  override def apply(baseUrl: String, credentials: Credentials): ProjectApi =
    new ProjectApi(baseUrl, credentials)
}