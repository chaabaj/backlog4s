package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object CommentApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def resource(issueIdOrKey: IdOrKeyParam[Issue]): String =
    s"issues/$issueIdOrKey/comments"

  def getAll(issueIdOrKey: IdOrKeyParam[Issue],
             minId: Option[Id[Comment]] = None,
             maxId: Option[Id[Comment]] = None,
             count: Long = 20,
             order: Order = Order.Desc): ApiPrg[Response[Seq[Comment]]] =
    get[Seq[Comment]](HttpQuery(resource(issueIdOrKey)))

  def count(issueIdOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(s"${resource(issueIdOrKey)}/count)")
    )

  def getById(issueIdOrKey: IdOrKeyParam[Issue], id: Id[Comment]): ApiPrg[Response[Comment]] =
    get[Comment](
      HttpQuery(s"${resource(issueIdOrKey)}/${id.value}")
    )

  def add(issueIdOrKey: IdOrKeyParam[Issue], form: AddCommentForm): ApiPrg[Response[Comment]] =
    post[AddCommentForm, Comment](
      HttpQuery(resource(issueIdOrKey)),
      form
    )

  def update(issueIdOrKey: IdOrKeyParam[Issue],
             id: Id[Comment],
             newContent: String): ApiPrg[Response[Comment]] =
    put[CustomForm, Comment](
      HttpQuery(s"${resource(issueIdOrKey)}/${id.value}"),
      Map(
        "content" -> newContent
      )
    )

  def notifications(issueIdOrKey: IdOrKeyParam[Issue], id: Id[Comment]): ApiPrg[Response[Seq[CommentNotification]]] =
    get[Seq[CommentNotification]](
      HttpQuery(s"${resource(issueIdOrKey)}/${id.value}/notifications")
    )

  def addNotification(issueIdOrKey: IdOrKeyParam[Issue],
                      id: Id[Comment],
                      form: AddCommentNotificationForm): ApiPrg[Response[Comment]] =
    post[AddCommentNotificationForm, Comment](
      HttpQuery(
        s"${resource(issueIdOrKey)}/${id.value}/notifications"
      ),
      form
    )
}
