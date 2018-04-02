package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.Response
import com.github.chaabaj.backlog4s.dsl.{HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class IssueCommentApi(override val baseUrl: String,
                      override val credentials: Credentials) extends Api {
  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  def resource(issueIdOrKey: IdOrKeyParam[Issue]): String =
    s"issues/$issueIdOrKey/comments"

  def allOf(issueIdOrKey: IdOrKeyParam[Issue],
            minId: Option[Id[Comment]] = None,
            maxId: Option[Id[Comment]] = None,
            count: Long = 20,
            order: Order = Order.Desc): ApiPrg[Response[Seq[Comment]]] = {
    val params = Seq(
      QueryParam.option("minId", minId),
      QueryParam.option("maxId", maxId),
      QueryParam("count", count.toString),
      QueryParam("order", order.toString)
    )

    get[Seq[Comment]](
      HttpQuery(
        resource(issueIdOrKey),
        params,
        credentials,
        baseUrl
      )
    )
  }

  def count(issueIdOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/count)",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def getById(issueIdOrKey: IdOrKeyParam[Issue], id: Id[Comment]): ApiPrg[Response[Comment]] =
    get[Comment](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(issueIdOrKey: IdOrKeyParam[Issue], form: AddCommentForm): ApiPrg[Response[Comment]] =
    post[AddCommentForm, Comment](
      HttpQuery(
        path = resource(issueIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(issueIdOrKey: IdOrKeyParam[Issue],
             id: Id[Comment],
             newContent: String): ApiPrg[Response[Comment]] =
    put[CustomForm, Comment](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "content" -> newContent
      )
    )

  def notifications(issueIdOrKey: IdOrKeyParam[Issue], id: Id[Comment]): ApiPrg[Response[Seq[Notification]]] =
    get[Seq[Notification]](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}/notifications",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def addNotification(issueIdOrKey: IdOrKeyParam[Issue],
                      id: Id[Comment],
                      form: AddNotificationForm): ApiPrg[Response[Comment]] =
    post[AddNotificationForm, Comment](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}/notifications",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )
}

object IssueCommentApi extends ApiContext[IssueCommentApi] {
  override def apply(baseUrl: String, credentials: Credentials): IssueCommentApi =
    new IssueCommentApi(baseUrl, credentials)
}
