package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class GroupApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {
  private val resource = "groups"

  def all(offset: Int = 0,
          limit: Int = 100,
          order: Order = Order.Desc): F[Response[Seq[Group]]] =
    BacklogHttpDsl.get[Seq[Group]](
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

  def byId(id: Id[Group]): F[Response[Group]] =
    BacklogHttpDsl.get[Group](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def create(addGroupForm: AddGroupForm): F[Response[Group]] =
    BacklogHttpDsl.post[AddGroupForm, Group](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      addGroupForm
    )

  def update(updateGroupForm: UpdateGroupForm): F[Response[Group]] =
    BacklogHttpDsl.put[UpdateGroupForm, Group](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      updateGroupForm
    )

  def remove(id: Id[Group]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/${id.value}", credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
