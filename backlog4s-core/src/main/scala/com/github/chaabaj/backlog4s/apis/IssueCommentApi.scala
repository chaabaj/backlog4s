package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class IssueCommentApi[F[_]](baseUrl: String,
                      credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def resource(issueIdOrKey: IdOrKeyParam[Issue]): String =
    s"issues/$issueIdOrKey/comments"

  def allOf(issueIdOrKey: IdOrKeyParam[Issue],
            minId: Option[Id[Comment]] = None,
            maxId: Option[Id[Comment]] = None,
            count: Long = 20,
            order: Order = Order.Desc): F[Response[Seq[Comment]]] = {
    val params = Seq(
      QueryParam.option("minId", minId),
      QueryParam.option("maxId", maxId),
      QueryParam("count", count.toString),
      QueryParam("order", order.toString)
    )

    BacklogHttpDsl.get[Seq[Comment]](
      HttpQuery(
        resource(issueIdOrKey),
        params,
        credentials,
        baseUrl
      )
    )
  }

  def count(issueIdOrKey: IdOrKeyParam[Issue]): F[Response[Count]] =
    BacklogHttpDsl.get[Count](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/count)",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def getById(issueIdOrKey: IdOrKeyParam[Issue], id: Id[Comment]): F[Response[Comment]] =
    BacklogHttpDsl.get[Comment](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(issueIdOrKey: IdOrKeyParam[Issue], form: AddCommentForm): F[Response[Comment]] =
    BacklogHttpDsl.post[AddCommentForm, Comment](
      HttpQuery(
        path = resource(issueIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(issueIdOrKey: IdOrKeyParam[Issue],
             id: Id[Comment],
             newContent: String): F[Response[Comment]] =
    BacklogHttpDsl.put[CustomForm, Comment](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "content" -> newContent
      )
    )

  def notifications(issueIdOrKey: IdOrKeyParam[Issue], id: Id[Comment]): F[Response[Seq[Notification]]] =
    BacklogHttpDsl.get[Seq[Notification]](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}/notifications",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def addNotification(issueIdOrKey: IdOrKeyParam[Issue],
                      id: Id[Comment],
                      form: AddNotificationForm): F[Response[Comment]] =
    BacklogHttpDsl.post[AddNotificationForm, Comment](
      HttpQuery(
        path = s"${resource(issueIdOrKey)}/${id.value}/notifications",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )
}
