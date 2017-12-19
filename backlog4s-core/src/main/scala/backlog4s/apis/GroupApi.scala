package backlog4s.apis

import backlog4s.datas.{AddGroupForm, Group, Id, UpdateGroupForm}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object GroupApi {
  private val resource = "groups"

  import backlog4s.dsl.ApiDsl.HttpOp._

  def getById(id: Id[Group]): ApiPrg[Response[Group]] =
    get[Group](HttpQuery(s"$resource/${id.value}"))

  def create(addGroupForm: AddGroupForm): ApiPrg[Response[Group]] =
    post[AddGroupForm, Group](
      HttpQuery(resource),
      addGroupForm
    )

  def update(updateGroupForm: UpdateGroupForm): ApiPrg[Response[Group]] =
    put[UpdateGroupForm, Group](
      HttpQuery(resource),
      updateGroupForm
    )

  def remove(id: Id[Group]): ApiPrg[Response[Unit]] =
    delete(HttpQuery(s"$resource/${id.value}"))
}
