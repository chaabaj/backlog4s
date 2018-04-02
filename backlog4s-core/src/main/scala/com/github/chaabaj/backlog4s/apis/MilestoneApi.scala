package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.Response
import com.github.chaabaj.backlog4s.dsl.HttpQuery
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class MilestoneApi(override val baseUrl: String,
                   override val credentials: Credentials) extends Api{

  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/versions"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[Milestone]]] =
    get[Seq[Milestone]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project],
          form: AddMilestoneForm): ApiPrg[Response[Milestone]] =
    post[AddMilestoneForm, Milestone](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Milestone],
             form: UpdateMilestoneForm): ApiPrg[Response[Milestone]] =
    put[UpdateMilestoneForm, Milestone](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project], id: Id[Milestone]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object MilestoneApi extends ApiContext[MilestoneApi] {
  override def apply(baseUrl: String, credentials: Credentials): MilestoneApi =
    new MilestoneApi(baseUrl, credentials)
}
