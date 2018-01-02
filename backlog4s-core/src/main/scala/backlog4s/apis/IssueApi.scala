package backlog4s.apis

import backlog4s.datas.{Issue, IssueSearch}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

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

    params.filter {
      case (_, value) if value.nonEmpty => true
      case _ => false
    }
  }

  def search(issueSearch: IssueSearch = IssueSearch()): ApiPrg[Response[Seq[Issue]]] =
    get[Seq[Issue]](
      HttpQuery(resource, searchParams(issueSearch))
    )
}
