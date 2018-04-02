package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.Response
import com.github.chaabaj.backlog4s.dsl.HttpQuery
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class IssueTypeApi(override val baseUrl: String,
                   override val credentials: Credentials) extends Api {
  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/issueTypes"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[IssueType]]] =
    get[Seq[IssueType]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project], color: RGBColor): ApiPrg[Response[IssueType]] =
    post[CustomForm, IssueType](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "color" -> color.toHex
      )
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[IssueType],
             form: UpdateIssueTypeForm): ApiPrg[Response[IssueType]] =
    put[UpdateIssueTypeForm, IssueType](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project], id: Id[IssueType]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object IssueTypeApi extends ApiContext[IssueTypeApi] {
  override def apply(baseUrl: String, credentials: Credentials): IssueTypeApi =
    new IssueTypeApi(baseUrl, credentials)
}
