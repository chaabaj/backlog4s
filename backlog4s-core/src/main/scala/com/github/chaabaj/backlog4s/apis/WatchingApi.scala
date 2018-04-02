package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas.NoContent.NoContent
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.Response
import com.github.chaabaj.backlog4s.dsl.{HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._


class WatchingApi(override val baseUrl: String,
                  override val credentials: Credentials) extends Api {
  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "watchings"

  def search(userId: Id[User],
             searchParams: WatchingSearch = WatchingSearch()): ApiPrg[Response[Seq[Watching]]] = {
    val params = Seq(
      QueryParam("order", searchParams.order.toString),
      QueryParam("sort", searchParams.sort.toString),
      QueryParam("count", searchParams.count),
      QueryParam("offset", searchParams.offset),
      QueryParam.option("resourceAlreadyRead", searchParams.resourceAlreadyRead),
      QueryParam.option("issueId", searchParams.issueId)
    )

    get[Seq[Watching]](
      HttpQuery(
        s"users/${userId.value}/$resource",
        params,
        credentials,
        baseUrl
      )
    )
  }

  def count(userId: Id[User],
            resourceAlreadyRead: Option[Boolean] = None,
            alreadyRead: Option[Boolean] = None): ApiPrg[Response[Count]] = {
    val params = Seq(
      QueryParam.option("resourceAlreadyRead", resourceAlreadyRead),
      QueryParam.option("alreadyRead", alreadyRead)
    )

    get[Count](
      HttpQuery(
        path = s"users/${userId.value}/$resource/count",
        params,
        credentials,
        baseUrl
      )
    )
  }

  def byId(id: Id[Watching]): ApiPrg[Response[Watching]] =
    get[Watching](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(form: AddWatchingForm): ApiPrg[Response[Watching]] =
    post[AddWatchingForm, Watching](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(id: Id[Watching], note: String): ApiPrg[Response[Watching]] =
    put[CustomForm, Watching](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "note" -> note
      )
    )

  def remove(id: Id[Watching]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def markAsRead(id: Id[Watching]): ApiPrg[Response[NoContent]] =
    post[CustomForm, NoContent](
      HttpQuery(
        path = s"$resource/${id.value}/markAsRead",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map()
    )
}

object WatchingApi extends ApiContext[WatchingApi] {
  override def apply(baseUrl: String, credentials: Credentials): WatchingApi =
    new WatchingApi(baseUrl, credentials)
}
