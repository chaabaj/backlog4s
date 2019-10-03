package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._
import cats.data.NonEmptyList
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}

class IssueApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

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

  def search(issueSearch: IssueSearch = IssueSearch()): F[Response[Seq[Issue]]] =
    BacklogHttpDsl.get[Seq[Issue]](
      HttpQuery(resource, searchParams(issueSearch), credentials, baseUrl)
    )

  def count(issueSearch: IssueSearch = IssueSearch()): F[Response[Count]] =
    BacklogHttpDsl.get[Count](
      HttpQuery(s"$resource/count", searchParams(issueSearch), credentials, baseUrl)
    )

  def byIdOrKey(issueIdOrKey: IdOrKeyParam[Issue]): F[Response[Issue]] =
    BacklogHttpDsl.get[Issue](
      HttpQuery(
        path = s"$resource/$issueIdOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def recentlyViewed(order: Order = Order.Desc,
                     offset: Long = 0,
                     count: Long = 20): F[Response[Seq[Issue]]] =
    BacklogHttpDsl.get[Seq[Issue]](
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

  def add(form: AddIssueForm): F[Response[Issue]] =
    BacklogHttpDsl.post[AddIssueForm, Issue](
      HttpQuery(path = resource, credentials = credentials, baseUrl = baseUrl),
      form
    )

  def update(idOrKey: IdOrKeyParam[Issue], form: UpdateIssueForm): F[Response[Issue]] =
    BacklogHttpDsl.put[UpdateIssueForm, Issue](
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(idOrKey: IdOrKeyParam[Issue]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/$idOrKey",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def linked(issueIdOrKey: IdOrKeyParam[Issue]): F[Response[Seq[SharedFile]]] =
    BacklogHttpDsl.get[Seq[SharedFile]](
      HttpQuery(
        path = s"$resource/$issueIdOrKey/sharedFiles",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadFile(projectIdOrKey: IdOrKeyParam[Project],
                   id: Id[SharedFile]): F[Response[ByteStream]] =
    BacklogHttpDsl.download(
      HttpQuery(
        path = s"projects/$projectIdOrKey/files/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def link(issueIdOrKey: IdOrKeyParam[Issue],
           fileIds: NonEmptyList[Id[SharedFile]]): F[Response[SharedFile]] =
    BacklogHttpDsl.post[LinkFilesForm, SharedFile](
      HttpQuery(
        path = s"$resource/$issueIdOrKey/sharedFiles",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      LinkFilesForm(fileIds.toList)
    )

  def unlink(issueIdOrKey: IdOrKeyParam[Issue],
             id: Id[SharedFile]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/$issueIdOrKey/sharedFiles/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def attach(idOrKey: IdOrKeyParam[Issue]): F[Response[Seq[Attachment]]] =
    BacklogHttpDsl.get[Seq[Attachment]](
      HttpQuery(
        path = s"$resource/$idOrKey/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadAttachment(idOrKey: IdOrKeyParam[Issue],
                         attachmentId: Id[Attachment]): F[Response[ByteStream]] =
    BacklogHttpDsl.download(
      HttpQuery(
        path = s"$resource/$idOrKey/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def removeAttachment(idOrKey: IdOrKeyParam[Issue],
                       attachmentId: Id[Attachment]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/$idOrKey/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
