package backlog4s.apis

import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._
import backlog4s.utils.QueryParameter

object IssueApi {
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
      HttpQuery(resource, searchParams(issueSearch))
    )

  def count(issueSearch: IssueSearch = IssueSearch()): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(s"$resource/count", searchParams(issueSearch))
    )

  def byIdOrKey(issueIdOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Issue]] =
    get[Issue](
      HttpQuery(
        path = s"$resource/$issueIdOrKey"
      )
    )

  def recentlyViewed(order: Order = Order.Desc,
                     offset: Long = 0,
                     count: Long = 20): ApiPrg[Response[Seq[Issue]]] =
    get[Seq[Issue]](
      HttpQuery("users/myself/recentlyViewedIssues")
    )

  def add(form: AddIssueForm): ApiPrg[Response[Issue]] =
    post[AddIssueForm, Issue](
      HttpQuery(resource),
      form
    )

  def update(idOrKey: IdOrKeyParam[Issue], form: UpdateIssueForm): ApiPrg[Response[Issue]] =
    put[UpdateIssueForm, Issue](
      HttpQuery(s"$resource/$idOrKey"),
      form
    )

  def remove(idOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Unit]] =
    delete(HttpQuery(s"$resource/$idOrKey"))

}
