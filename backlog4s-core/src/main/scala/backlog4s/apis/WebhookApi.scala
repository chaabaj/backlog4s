package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object WebhookApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/webhooks"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[Webhook]]] =
    get[Seq[Webhook]](
      HttpQuery(resource(projectIdOrKey))
    )

  def add(projectIdOrKey: IdOrKeyParam[Project],
          form: AddWebhookForm): ApiPrg[Response[Webhook]] =
    post[AddWebhookForm, Webhook](
      HttpQuery(
        resource(projectIdOrKey)
      ),
      form
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Webhook],
             form: UpdateWebhookForm): ApiPrg[Response[Webhook]] =
    put[UpdateWebhookForm, Webhook](
      HttpQuery(
        s"${resource(projectIdOrKey)}/${id.value}"
      ),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Webhook]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        s"${resource(projectIdOrKey)}/${id.value}"
      )
    )
}
