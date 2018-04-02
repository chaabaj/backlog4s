package backlog4s.apis

import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.{HttpQuery, QueryParam}
import backlog4s.formatters.SprayJsonFormats._

class GroupApi(override val baseUrl: String,
               override val credentials: Credentials) extends Api {
  private val resource = "groups"

  import backlog4s.dsl.ApiDsl.HttpOp._

  def all(offset: Int = 0,
          limit: Int = 100,
          order: Order = Order.Desc): ApiPrg[Response[Seq[Group]]] =
    get[Seq[Group]](
      HttpQuery(
        s"$resource",
        Seq(
          QueryParam("offset", offset),
          QueryParam("count", limit),
          QueryParam("order", order.toString)
        ),
        credentials,
        baseUrl = baseUrl
      )
    )

  def byId(id: Id[Group]): ApiPrg[Response[Group]] =
    get[Group](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def create(addGroupForm: AddGroupForm): ApiPrg[Response[Group]] =
    post[AddGroupForm, Group](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      addGroupForm
    )

  def update(updateGroupForm: UpdateGroupForm): ApiPrg[Response[Group]] =
    put[UpdateGroupForm, Group](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      updateGroupForm
    )

  def remove(id: Id[Group]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/${id.value}", credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object GroupApi extends ApiContext[GroupApi] {
  override def apply(baseUrl: String, credentials: Credentials): GroupApi =
    new GroupApi(baseUrl, credentials)
}
