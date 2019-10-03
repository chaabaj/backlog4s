package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class WebhookApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/webhooks"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Seq[Webhook]]] =
    BacklogHttpDsl.get[Seq[Webhook]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project],
          form: AddWebhookForm): F[Response[Webhook]] =
    BacklogHttpDsl.post[AddWebhookForm, Webhook](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Webhook],
             form: UpdateWebhookForm): F[Response[Webhook]] =
    BacklogHttpDsl.put[UpdateWebhookForm, Webhook](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Webhook]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
