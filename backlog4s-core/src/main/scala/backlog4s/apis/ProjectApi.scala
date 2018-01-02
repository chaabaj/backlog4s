package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object ProjectApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "projects"

  def getAll(offset: Int = 0, count: Int = 100,
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

  def getById(id: Id[Project]): ApiPrg[Response[Project]] =
    get[Project](HttpQuery(s"$resource/${id.value}"))

  def getByKey(key: Key[Project]): ApiPrg[Response[Project]] =
    get[Project](HttpQuery(s"$resource/${key.value}"))

  def getAdmins(id: Id[Project]): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](HttpQuery(s"$resource/${id.value}/administrators"))

  def getAdmins(key: Key[Project]): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](HttpQuery(s"$resource/${key.value}/administrators"))

  def create(addProjectForm: AddProjectForm): ApiPrg[Response[Project]] =
    post[AddProjectForm, Project](HttpQuery(resource), addProjectForm)

  private def unsafeAddAdmin(projectIdOrKey: String, userId: Id[User]): ApiPrg[Response[User]] =
    post[CustomForm, User](
      HttpQuery(s"$resource/$projectIdOrKey/administrators"),
      Map(
        "userId" -> userId.value.toString
      )
    )

  def addAdmin(projectId: Id[Project], userId: Id[User]): ApiPrg[Response[User]] =
    unsafeAddAdmin(projectId.value.toString, userId)

  def addAdmin(projectKey: Key[Project], userId: Id[User]): ApiPrg[Response[User]] =
    unsafeAddAdmin(projectKey.value, userId)

  private def unsafeRemoveAdmin(projectIdOrKey: String, userId: Id[User]): ApiPrg[Response[Unit]] =
    delete(HttpQuery(
      path = s"$resource/$projectIdOrKey/administrators",
      params = Map(
        "userId" -> userId.value.toString
      )
    ))

  def removeAdmin(projectId: Id[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    unsafeRemoveAdmin(projectId.value.toString, userId)

  def removeAdmin(projectKey: Key[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    unsafeRemoveAdmin(projectKey.value, userId)

  private def unsafeAddUser(projectIdOrKey: String, userId: Id[User]): ApiPrg[Response[User]] =
    post[CustomForm, User](
      HttpQuery(
        s"$resource/$projectIdOrKey/users"
      ),
      Map(
        "userId" -> userId.toString
      )
    )

  def addUser(projectId: Id[Project], userId: Id[User]): ApiPrg[Response[User]] =
    unsafeAddUser(projectId.value.toString, userId)

  def addUser(projectKey: Key[Project], userId: Id[User]): ApiPrg[Response[User]] =
    unsafeAddUser(projectKey.value, userId)

  private def unsafeRemoveUser(projectIdOrKey: String, userId: Id[User]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/$projectIdOrKey/users",
        params = Map(
          "userId" -> userId.value.toString
        )
      )
    )

  def removeUser(projectId: Id[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    unsafeRemoveUser(projectId.value.toString, userId)

  def removeUser(projectKey: Key[Project], userId: Id[User]): ApiPrg[Response[Unit]] =
    unsafeRemoveUser(projectKey.value, userId)

  def update(id: Id[Project], form: UpdateProjectForm): ApiPrg[Response[Project]] =
    put[UpdateProjectForm, Project](
      HttpQuery(s"$resource/${id.value}"), form
    )

  def update(key: Key[Project], form: UpdateProjectForm): ApiPrg[Response[Project]] =
    put[UpdateProjectForm, Project](
      HttpQuery(s"$resource/${key.value}"), form
    )
}
