package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object ProjectApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "projects"

  def getAll(offset: Int, count: Int,
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

  def create(addProjectForm: AddProjectForm): ApiPrg[Response[Project]] =
    post[AddProjectForm, Project](HttpQuery(resource), addProjectForm)


  def update(id: Id[Project], form: UpdateProjectForm): ApiPrg[Response[Project]] =
    put[UpdateProjectForm, Project](
      HttpQuery(s"$resource/${id.value}"), form
    )

  def update(key: Key[Project], form: UpdateProjectForm): ApiPrg[Response[Project]] =
    put[UpdateProjectForm, Project](
      HttpQuery(s"$resource/${key.value}"), form
    )
}
