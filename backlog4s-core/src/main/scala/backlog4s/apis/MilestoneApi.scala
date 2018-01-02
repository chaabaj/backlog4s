package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object MilestoneApi {

  import backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/versions"

  def getAll(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[Milestone]]] =
    get[Seq[Milestone]](
      HttpQuery(resource(projectIdOrKey))
    )

  def add(projectIdOrKey: IdOrKeyParam[Project],
          form: AddMilestoneForm): ApiPrg[Response[Milestone]] =
    post[AddMilestoneForm, Milestone](
      HttpQuery(resource(projectIdOrKey)),
      form
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Milestone],
             form: UpdateMilestoneForm): ApiPrg[Response[Milestone]] =
    put[UpdateMilestoneForm, Milestone](
      HttpQuery(resource(projectIdOrKey)),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project], id: Id[Milestone]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(s"${resource(projectIdOrKey)}/${id.value}")
    )
}
