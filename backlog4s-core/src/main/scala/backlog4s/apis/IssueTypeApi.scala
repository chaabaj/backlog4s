package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object IssueTypeApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/issueTypes"

  def getAll(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[IssueType]]] =
    get[Seq[IssueType]](
      HttpQuery(resource(projectIdOrKey))
    )

  def add(projectIdOrKey: IdOrKeyParam[Project], color: RGBColor): ApiPrg[Response[IssueType]] =
    post[CustomForm, IssueType](
      HttpQuery(resource(projectIdOrKey)),
      Map(
        "color" -> color.toHex
      )
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[IssueType],
             form: UpdateIssueTypeForm): ApiPrg[Response[IssueType]] =
    put[UpdateIssueTypeForm, IssueType](
      HttpQuery(s"${resource(projectIdOrKey)}/${id.value}"),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project], id: Id[IssueType]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(s"${resource(projectIdOrKey)}/${id.value}")
    )
}
