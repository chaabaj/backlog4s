package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas.NoContent.NoContent
import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.utils.QueryParameter
import backlog4s.formatters.SprayJsonFormats._

class NotificationApi(override val baseUrl: String,
                      override val credentials: Credentials) extends Api {
  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "notifications"

  def all(minId: Option[Id[Notification]] = None,
          maxId: Option[Id[Notification]] = None,
          count: Long = 20,
          order: Order = Order.Desc): ApiPrg[Response[Seq[Notification]]] = {
    val params = Map(
      "minId" -> minId.map(_.value.toString).getOrElse(""),
      "maxId" -> maxId.map(_.value.toString).getOrElse(""),
      "count" -> count.toString,
      "order" -> order.toString
    )
    get[Seq[Notification]](
      HttpQuery(
        resource,
        QueryParameter.removeEmptyValue(params),
        credentials,
        baseUrl
      )
    )
  }

  def count(resourceAlreadyRead: Option[Boolean] = None,
            alreadyRead: Option[Boolean] = None): ApiPrg[Response[Count]] = {
    val params = Map(
      "resourceAlreadyRead" -> resourceAlreadyRead.map(_.toString).getOrElse(""),
      "alreadyRead" -> alreadyRead.map(_.toString).getOrElse("")
    )
    get[Count](
      HttpQuery(
        s"$resource/count",
        QueryParameter.removeEmptyValue(params),
        credentials,
        baseUrl
      )
    )
  }

  def markAllAsRead: ApiPrg[Response[Count]] =
    post[CustomForm, Count](
      HttpQuery(
        path = s"$resource/markAsRead",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map()
    )

  def read(id: Id[Notification]): ApiPrg[Response[NoContent]] =
    post[CustomForm, NoContent](
      HttpQuery(
        path = s"notifications/${id.value}/markAsRead",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map()
    )
}

object NotificationApi extends ApiContext[NotificationApi] {
  override def apply(baseUrl: String, credentials: Credentials): NotificationApi =
    new NotificationApi(baseUrl, credentials)
}