package com.github.chaabaj.backlog4s.datas

import com.github.chaabaj.backlog4s.datas.IssueSearchBy.IssueSearchBy
import com.github.chaabaj.backlog4s.datas.Order.Order
import org.joda.time.DateTime

object IssueT {
  def id(value: Long): Id[Issue] = Id(value)
  def key(value: String): Key[Issue] = Key(value)
}

case class Issue(
  id: Id[Issue],
  projectId: Id[Project],
  keyId: Long,
  issueType: IssueType,
  summary: String,
  description: Option[String],
  resolution: Option[Resolution],
  priority: Priority,
  status: Status,
  assignee: Option[User],
  category: Seq[Category],
  milestone: Seq[Milestone],
  createdUser: User,
  created: DateTime,
  updatedUser: Option[User],
  updated: Option[DateTime],
  //customField not supported yet
  attachments: Seq[Attachment],
  sharedFiles: Seq[SharedFile],
  stars: Seq[Star]
)

object IssueSearchBy extends Enumeration {
  type IssueSearchBy = Value

  val IssueType = Value("issueType")
  val Category = Value("category")
  val Version = Value("version")
  val Milestone = Value("milestone")
  val Summary = Value("summary")
  val Status = Value("status")
  val Priority = Value("priority")
  val Attachment = Value("attachment")
  val SharedFile = Value("sharedFile")
  val Created = Value("created")
  val CreatedUser = Value("createdUser")
  val Updated = Value("updated")
  val UpdatedUser = Value("updatedUser")
  val Assignee = Value("assignee")
  val StartDate = Value("startDate")
  val DueDate = Value("dueDate")
  val EstimatedHours = Value("estimatedHours")
  val ActualHours = Value("actualHours")
  val ChildIssue = Value("childIssue")

  def customField(fieldName: String): IssueSearchBy =
    Value(s"customField_$fieldName")
}

case class IssueSearch(
                        projectIds: Seq[Id[Project]] = Seq(),
                        issueTypeIds: Seq[Id[IssueType]] = Seq(),
                        categoryIds: Seq[Id[Category]] = Seq(),
                        //versionIds: Seq[Id[Milestone]] = Seq(),
                        milestoneIds: Seq[Id[Milestone]] = Seq(),
                        statusIds: Seq[Id[Status]] = Seq(),
                        priorityIds: Seq[Id[Priority]] = Seq(),
                        assigneeIds: Seq[Id[User]] = Seq(),
                        createdUserIds: Seq[Id[User]] = Seq(),
                        resolutionIds: Seq[Id[Resolution]] = Seq(),
                        offset: Int = 0,
                        //parentChild
                        attachment: Option[Boolean] = None,
                        sharedFile: Option[Boolean] = None,
                        sort: Option[IssueSearchBy] = None,
                        order: Order = Order.Desc,
                        count: Long = 100,
                        createdSince: Option[DateTime] = None,
                        createdUntil: Option[DateTime] = None,
                        updatedSince: Option[DateTime] = None,
                        updatedUntil: Option[DateTime] = None,
                        startDateSince: Option[DateTime] = None,
                        startDateUntil: Option[DateTime] = None,
                        dueDateSince: Option[DateTime] = None,
                        dueDateUntil: Option[DateTime] = None,
                        ids: Seq[Id[Issue]] = Seq(),
                        parentIssueIds: Seq[Id[Issue]] = Seq(),
                        keyword: Option[String] = None
)

case class AddIssueForm(
  projectId: Id[Project],
  summary: String,
  parentIssueId: Seq[Id[Issue]] = Seq(),
  description: Option[String] = None,
  startDate: Option[DateTime] = None,
  dueDate: Option[DateTime] = None,
  estimatedHours: Int,
  actualHours: Int,
  issueTypeId: Id[IssueType],
  categoryId: Seq[Category] = Seq(),
  // versionId
  milestoneId: Seq[Milestone] = Seq(),
  priorityId: Id[Priority],
  assigneeId: Option[Id[User]] = None,
  notifiedUserId: Seq[Id[User]] = Seq(),
  attachmentId: Seq[Id[Attachment]] = Seq()
)

case class UpdateIssueForm(
  summary: Option[String] = None,
  parentIssueId: Option[Id[Issue]] = None,
  description: Option[String] = None,
  statusId: Option[Id[Status]] = None,
  resolutionId: Option[Id[Resolution]] = None,
  startDate: Option[DateTime] = None,
  dueDate: Option[DateTime] = None,
  estimatedHours: Option[Int] = None,
  actualHours: Option[Int] = None,
  // Very strange for update need confirmation from backlog team
  issueTypeId: Id[IssueType],
  categoryId: Option[Seq[Category]] = None,
  // versionId
  milestoneId: Option[Seq[Milestone]] = None,
  priorityId: Id[Priority],
  assigneeId: Option[Id[User]] = None,
  notifiedUserId: Option[Seq[Id[User]]] = None,
  attachmentId: Option[Seq[Id[Attachment]]] = None,
  comment: Option[String] = None
)