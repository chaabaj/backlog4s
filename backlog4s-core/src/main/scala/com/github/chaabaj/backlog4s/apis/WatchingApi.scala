package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas.NoContent.NoContent
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._


class WatchingApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  val resource = "watchings"

  def search(userId: Id[User],
             searchParams: WatchingSearch = WatchingSearch()): F[Response[Seq[Watching]]] = {
    val params = Seq(
      QueryParam("order", searchParams.order.toString),
      QueryParam("sort", searchParams.sort.toString),
      QueryParam("count", searchParams.count),
      QueryParam("offset", searchParams.offset),
      QueryParam.option("resourceAlreadyRead", searchParams.resourceAlreadyRead),
      QueryParam.option("issueId", searchParams.issueId)
    )

    BacklogHttpDsl.get[Seq[Watching]](
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
            alreadyRead: Option[Boolean] = None): F[Response[Count]] = {
    val params = Seq(
      QueryParam.option("resourceAlreadyRead", resourceAlreadyRead),
      QueryParam.option("alreadyRead", alreadyRead)
    )

    BacklogHttpDsl.get[Count](
      HttpQuery(
        path = s"users/${userId.value}/$resource/count",
        params,
        credentials,
        baseUrl
      )
    )
  }

  def byId(id: Id[Watching]): F[Response[Watching]] =
    BacklogHttpDsl.get[Watching](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(form: AddWatchingForm): F[Response[Watching]] =
    BacklogHttpDsl.post[AddWatchingForm, Watching](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(id: Id[Watching], note: String): F[Response[Watching]] =
    BacklogHttpDsl.put[CustomForm, Watching](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "note" -> note
      )
    )

  def remove(id: Id[Watching]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def markAsRead(id: Id[Watching]): F[Response[NoContent]] =
    BacklogHttpDsl.post[CustomForm, NoContent](
      HttpQuery(
        path = s"$resource/${id.value}/markAsRead",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map()
    )
}
