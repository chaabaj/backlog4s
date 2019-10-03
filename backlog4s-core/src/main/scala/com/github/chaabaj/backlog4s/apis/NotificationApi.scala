package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas.NoContent.NoContent
import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class NotificationApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  val resource = "notifications"

  def all(minId: Option[Id[Notification]] = None,
          maxId: Option[Id[Notification]] = None,
          count: Long = 20,
          order: Order = Order.Desc): F[Response[Seq[Notification]]] = {
    val params = Seq(
      QueryParam.option("minId", minId),
      QueryParam.option("maxId", maxId),
      QueryParam("count", count.toString),
      QueryParam("order", order.toString)
    )

    BacklogHttpDsl.get[Seq[Notification]](
      HttpQuery(
        resource,
        params,
        credentials,
        baseUrl
      )
    )
  }

  def count(resourceAlreadyRead: Option[Boolean] = None,
            alreadyRead: Option[Boolean] = None): F[Response[Count]] = {
    val params = Seq(
      QueryParam.option("resourceAlreadyRead", resourceAlreadyRead),
      QueryParam.option("alreadyRead", alreadyRead)
    )
    BacklogHttpDsl.get[Count](
      HttpQuery(
        s"$resource/count",
        params,
        credentials,
        baseUrl
      )
    )
  }

  def markAllAsRead: F[Response[Count]] =
    BacklogHttpDsl.post[CustomForm, Count](
      HttpQuery(
        path = s"$resource/markAsRead",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map()
    )

  def read(id: Id[Notification]): F[Response[NoContent]] =
    BacklogHttpDsl.post[CustomForm, NoContent](
      HttpQuery(
        path = s"notifications/${id.value}/markAsRead",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map()
    )
}
