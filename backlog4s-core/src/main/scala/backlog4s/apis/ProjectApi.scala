package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object ProjectApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "projects"

  def all(offset: Int = 0, count: Int = 100,
          archived: Option[Boolean] = None,
          all: Boolean = false): ApiPrg[Response[Seq[Project]]] = {
    val params = archived.map(archived => Map(
      "offset" -> offset.toString,
      "count" -> count.toString,
      "archived" -> archived.toString,
      "all" -> all.toString
    )).getOrElse(Map(
      "offset" -> offset.toString,
      "count" -> count.toString,
      "all" -> all.toString
    ))

    get[Seq[Project]](HttpQuery(resource, params))
  }

  def byIdOrKey(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Project]] =
    get[Project](HttpQuery(s"$resource/$id"))

  def admins(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](HttpQuery(s"$resource/$idOrKey/administrators"))

  def users(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](HttpQuery(s"$resource/$idOrKey/users"))

  def icon(idOrKey: IdOrKeyParam[Project]): ApiPrg[Response[ByteStream]] =
    download(HttpQuery(s"$resource/$idOrKey/image"))

  def recentlyViewed(order: Order = Order.Desc): ApiPrg[Response[Seq[Project]]] =
    get[Seq[Project]](
      HttpQuery(
        "users/myself/recentlyViewedProjects"
      )
    )

  def create(addProjectForm: AddProjectForm): ApiPrg[Response[Project]] =
    post[AddProjectForm, Project](HttpQuery(resource), addProjectForm)

  def addAdmin(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[User]] =
    post[CustomForm, User](
      HttpQuery(s"$resource/$idOrKey/administrators"),
      Map(
        "userId" -> userId.value.toString
      )
    )

  def removeAdmin(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    delete(HttpQuery(
      path = s"$resource/$idOrKey/administrators",
      params = Map(
        "userId" -> userId.value.toString
      )
    ))

  def addUser(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[User]] =
    post[CustomForm, User](
      HttpQuery(
        s"$resource/$idOrKey/users"
      ),
      Map(
        "userId" -> userId.toString
      )
    )

  def removeUser(idOrKey: IdOrKeyParam[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/$idOrKey/users",
        params = Map(
          "userId" -> userId.value.toString
        )
      )
    )

  def update(idOrKey: IdOrKeyParam[Project], form: UpdateProjectForm): ApiPrg[Response[Project]] =
    put[UpdateProjectForm, Project](
      HttpQuery(s"$resource/$idOrKey"), form
    )
}
