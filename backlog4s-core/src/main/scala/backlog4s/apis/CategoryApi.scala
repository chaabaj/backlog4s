package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas.{Category, Id, IdOrKeyParam, Project}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object CategoryApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/categories"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[Category]]] =
    get[Seq[Category]](
      HttpQuery(resource(projectIdOrKey))
    )

  def add(projectIdOrKey: IdOrKeyParam[Project], name: String): ApiPrg[Response[Category]] =
    post[CustomForm, Category](
      HttpQuery(resource(projectIdOrKey)),
      Map(
        "name" -> name
      )
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Category],
             newName: String): ApiPrg[Response[Category]] =
    put[CustomForm, Category](
      HttpQuery(
        s"${resource(projectIdOrKey)}/${id.value}"
      ),
      Map(
        "name" -> newName
      )
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Category]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(s"${resource(projectIdOrKey)}/${id.value}")
    )
}
