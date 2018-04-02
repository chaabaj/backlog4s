package com.github.backlog4s.apis

import com.github.backlog4s.datas.Order.Order
import com.github.backlog4s.datas._
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.HttpADT.{ByteStream, Response}
import com.github.backlog4s.dsl.{QueryParam, HttpQuery}
import com.github.backlog4s.formatters.SprayJsonFormats._
import cats.data.NonEmptyList

class IssueApi(override val baseUrl: String,
               override val credentials: Credentials) extends Api {
  import com.github.backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "issues"

  private def formatIdListParameter[A](name: String, ids: Seq[Id[A]]): String = {
    ids.zipWithIndex.map {
      case (id, index) =>
        s"$name[$index]=${id.value}"
    }.mkString("&")
  }

  private def searchParams(issueSearch: IssueSearch): Seq[QueryParam] = {
    val queryParams = Seq(
      QueryParam("projectId", issueSearch.projectIds),
      QueryParam("issueTypeId", issueSearch.issueTypeIds),
      QueryParam("categoryId", issueSearch.categoryIds),
      QueryParam("milestoneId", issueSearch.milestoneIds),
      QueryParam("statusId", issueSearch.statusIds),
      QueryParam("priorityId", issueSearch.priorityIds),
      QueryParam("assigneeId", issueSearch.assigneeIds),
      QueryParam("createdUserId", issueSearch.createdUserIds),
      QueryParam("resolutionId", issueSearch.resolutionIds),
      QueryParam.option("attachment", issueSearch.attachment),
      QueryParam.option("sharedFile", issueSearch.sharedFile),
      QueryParam.option("sort", issueSearch.sort.map(_.toString)),
      QueryParam("order", issueSearch.order.toString),
      QueryParam("count", issueSearch.count),
      QueryParam.option("createdSince", issueSearch.createdSince),
      QueryParam.option("createdUntil", issueSearch.createdUntil),
      QueryParam.option("updatedSince", issueSearch.updatedSince),
      QueryParam.option("updatedUntil", issueSearch.updatedUntil),
      QueryParam.option("startDateSince", issueSearch.startDateSince),
      QueryParam.option("startDateUntil", issueSearch.startDateUntil),
      QueryParam.option("dueDateSince", issueSearch.dueDateSince),
      QueryParam.option("dueDateUntil", issueSearch.dueDateUntil),
      QueryParam("id", issueSearch.ids),
      QueryParam("parentIssueId", issueSearch.parentIssueIds),
      QueryParam.option("keyword", issueSearch.keyword)
    )
    queryParams
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
        params = Seq(
          QueryParam("offset", offset),
          QueryParam("count", count)
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