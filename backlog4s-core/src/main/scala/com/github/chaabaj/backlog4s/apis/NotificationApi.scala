package com.github.backlog4s.apis

import com.github.backlog4s.datas.CustomForm.CustomForm
import com.github.backlog4s.datas.NoContent.NoContent
import com.github.backlog4s.datas.Order.Order
import com.github.backlog4s.datas._
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.HttpADT.Response
import com.github.backlog4s.dsl.{HttpQuery, QueryParam}
import com.github.backlog4s.formatters.SprayJsonFormats._

class NotificationApi(override val baseUrl: String,
                      override val credentials: Credentials) extends Api {
  import com.github.backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "notifications"

  def all(minId: Option[Id[Notification]] = None,
          maxId: Option[Id[Notification]] = None,
          count: Long = 20,
          order: Order = Order.Desc): ApiPrg[Response[Seq[Notification]]] = {
    val params = Seq(
      QueryParam.option("minId", minId),
      QueryParam.option("maxId", maxId),
      QueryParam("count", count.toString),
      QueryParam("order", order.toString)
    )

    get[Seq[Notification]](
      HttpQuery(
        resource,
        params,
        credentials,
        baseUrl
      )
    )
  }

  def count(resourceAlreadyRead: Option[Boolean] = None,
            alreadyRead: Option[Boolean] = None): ApiPrg[Response[Count]] = {
    val params = Seq(
      QueryParam.option("resourceAlreadyRead", resourceAlreadyRead),
      QueryParam.option("alreadyRead", alreadyRead)
    )
    get[Count](
      HttpQuery(
        s"$resource/count",
        params,
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