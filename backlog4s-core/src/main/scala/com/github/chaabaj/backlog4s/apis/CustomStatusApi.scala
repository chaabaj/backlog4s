package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class CustomStatusApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/statuses"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Seq[CustomStatus]]] =
    BacklogHttpDsl.get[Seq[CustomStatus]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project], name: String, color: CustomStatusColor): F[Response[CustomStatus]] =
    BacklogHttpDsl.post[CustomForm, CustomStatus](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "name" -> name,
        "color" -> color.hex
      )
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[CustomStatus],
             form: UpdateCustomStatusForm): F[Response[CustomStatus]] =
    BacklogHttpDsl.put[UpdateCustomStatusForm, CustomStatus](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def updateDisplayOrder(projectIdOrKey: IdOrKeyParam[Project],
                         form: UpdateCustomStatusDisplayOrderForm): F[Response[Seq[CustomStatus]]] =
    BacklogHttpDsl.put[UpdateCustomStatusDisplayOrderForm, Seq[CustomStatus]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project], id: Id[CustomStatus], form: DeleteCustomStatusForm): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )
}
