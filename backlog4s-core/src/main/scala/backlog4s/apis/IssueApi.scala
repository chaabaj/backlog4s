package backlog4s.apis

import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._
import backlog4s.utils.QueryParameter
import cats.data.NonEmptyList

class IssueApi(override val baseUrl: String,
               override val credentials: Credentials) extends Api {
  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "issues"

  private def searchParams(issueSearch: IssueSearch): Map[String, String] = {
    val params = Map(
      "projectId" -> issueSearch.assigneeIds.mkString(","),
      "issueTypeId" -> issueSearch.issueTypeIds.mkString(","),
      "categoryId" -> issueSearch.categoryIds.mkString(","),
      "milestoneId" -> issueSearch.milestoneIds.mkString(","),
      "statusId" -> issueSearch.statusIds.mkString(","),
      "priorityId" -> issueSearch.priorityIds.mkString(","),
      "assigneeId" -> issueSearch.assigneeIds.mkString(","),
      "createdUserId" -> issueSearch.createdUserIds.mkString(","),
      "resolutionId" -> issueSearch.resolutionId.mkString(","),
      "attachment" -> issueSearch.attachment.map(_.toString).getOrElse(""),
      "sharedFile" -> issueSearch.sharedFile.map(_.toString).getOrElse(""),
      "sort" -> issueSearch.sort.map(_.toString).getOrElse(""),
      "order" -> issueSearch.order.toString,
      "count" -> issueSearch.count.toString,
      "offset" -> issueSearch.offset.toString,
      "createdSince" -> issueSearch.createdSince
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "createdUntil" -> issueSearch.createdUntil
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "updatedSince" -> issueSearch.updatedSince
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "updatedUntil" -> issueSearch.updatedUntil
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "startDateSince" -> issueSearch.startDateSince
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "startDateUntil" -> issueSearch.startDateUntil
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "dueDateSince" -> issueSearch.dueDateSince
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "dueDateUntil" -> issueSearch.dueDateUntil
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "id" -> issueSearch.ids.mkString(","),
      "parentIssueId" -> issueSearch.parentIssueIds.mkString(","),
      "keyword" -> issueSearch.keyword.getOrElse("")
    )

    QueryParameter.removeEmptyValue(params)
  }

  def search(issueSearch: IssueSearch = IssueSearch()): ApiPrg[Response[Seq[Issue]]] =
    get[Seq[Issue]](
      HttpQuery(resource, searchParams(issueSearch), credentials, baseUrl)
    )

  def count(issueSearch: IssueSearch = IssueSearch()): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(s"$resource/count", searchParams(issueSearch), credentials, baseUrl)
    )

  def byIdOrKey(issueIdOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Issue]] =
    get[Issue](
      HttpQuery(
        path = s"$resource/$issueIdOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def recentlyViewed(order: Order = Order.Desc,
                     offset: Long = 0,
                     count: Long = 20): ApiPrg[Response[Seq[Issue]]] =
    get[Seq[Issue]](
      HttpQuery(
        path = "users/myself/recentlyViewedIssues",
        credentials = credentials,
        baseUrl = baseUrl,
        params = Map(
          "offset" -> offset.toString,
          "count" -> count.toString
        )
      )
    )

  def add(form: AddIssueForm): ApiPrg[Response[Issue]] =
    post[AddIssueForm, Issue](
      HttpQuery(path = resource, credentials = credentials, baseUrl = baseUrl),
      form
    )

  def update(idOrKey: IdOrKeyParam[Issue], form: UpdateIssueForm): ApiPrg[Response[Issue]] =
    put[UpdateIssueForm, Issue](
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(idOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def linked(issueIdOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Seq[SharedFile]]] =
    get[Seq[SharedFile]](
      HttpQuery(
        path = s"$resource/$issueIdOrKey/sharedFiles",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadFile(projectIdOrKey: IdOrKeyParam[Project],
                   id: Id[SharedFile]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        path = s"projects/$projectIdOrKey/files/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def link(issueIdOrKey: IdOrKeyParam[Issue],
           fileIds: NonEmptyList[Id[SharedFile]]): ApiPrg[Response[SharedFile]] =
    post[LinkFilesForm, SharedFile](
      HttpQuery(
        path = s"$resource/$issueIdOrKey/sharedFiles",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      LinkFilesForm(fileIds.toList)
    )

  def unlink(issueIdOrKey: IdOrKeyParam[Issue],
             id: Id[SharedFile]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/$issueIdOrKey/sharedFiles/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def attach(idOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Seq[Attachment]]] =
    get[Seq[Attachment]](
      HttpQuery(
        path = s"$resource/$idOrKey/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadAttachment(idOrKey: IdOrKeyParam[Issue],
                         attachmentId: Id[Attachment]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        path = s"$resource/$idOrKey/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def removeAttachment(idOrKey: IdOrKeyParam[Issue],
                       attachmentId: Id[Attachment]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/$idOrKey/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object IssueApi extends ApiContext[IssueApi] {
  override def apply(baseUrl: String, credentials: Credentials): IssueApi =
    new IssueApi(baseUrl, credentials)
}