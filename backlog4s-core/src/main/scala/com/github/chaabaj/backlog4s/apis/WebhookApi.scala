package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.Response
import com.github.chaabaj.backlog4s.dsl.HttpQuery
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class WebhookApi(override val baseUrl: String,
                 override val credentials: Credentials) extends Api {
  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/webhooks"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[Webhook]]] =
    get[Seq[Webhook]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project],
          form: AddWebhookForm): ApiPrg[Response[Webhook]] =
    post[AddWebhookForm, Webhook](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Webhook],
             form: UpdateWebhookForm): ApiPrg[Response[Webhook]] =
    put[UpdateWebhookForm, Webhook](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Webhook]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object WebhookApi extends ApiContext[WebhookApi] {
  override def apply(baseUrl: String, credentials: Credentials): WebhookApi =
    new WebhookApi(baseUrl, credentials)
}
