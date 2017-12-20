package backlog4s.apis

import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object GroupApi {
  private val resource = "groups"

  import backlog4s.dsl.ApiDsl.HttpOp._

  def getAll(offset: Int = 0,
             limit: Int = 100,
             order: Order = Order.Desc): ApiPrg[Response[Seq[Group]]] =
    get[Seq[Group]](HttpQuery(s"$resource", Map(
      "offset" -> offset.toString,
      "count" -> limit.toString,
      "order" -> order.toString
    )))

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
