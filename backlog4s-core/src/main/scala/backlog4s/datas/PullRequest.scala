package backlog4s.datas

import backlog4s.datas.PRStatusType.PRStatusType
import org.joda.time.DateTime

object PRStatusType extends Enumeration {
  type PRStatusType = Value

  val Open = Value(1, "Open")
  val Closed = Value(2, "Closed")
  val Merged = Value(3, "Merge")
}

case class PullRequestSummary(
  id: Id[PullRequest],
  projectId: Id[Project],
  repositoryId: Id[GitRepository],
  number: Long,
  summary: String,
  description: String,
  base: String,
  branch: String,
  status: PullRequestStatus,
  assignee: User,
  issue: Option[PullRequestIssue],
  baseCommit: String,
  branchCommit: String,
  closedAt: Option[DateTime],
  mergedAt: Option[DateTime],
  created: DateTime,
  createdUser: User,
  updatedUser: Option[User],
  updated: Option[DateTime]
)

case class PullRequest(
  id: Id[PullRequest],
  projectId: Id[Project],
  repositoryId: Id[GitRepository],
  number: Long,
  summary: String,
  description: String,
  base: String,
  branch: String,
  status: PullRequestStatus,
  assignee: User,
  issue: Option[Issue],
  baseCommit: String,
  branchCommit: String,
  closedAt: Option[DateTime],
  mergedAt: Option[DateTime],
  created: DateTime,
  createdUser: User,
  updatedUser: Option[User],
  updated: Option[DateTime],
  attachments: Seq[Attachment],
  stars: Seq[Star]
)

case class PullRequestStatus(
  id: Id[PullRequestStatus],
  name: PRStatusType
)

case class PullRequestIssue(
  id: Id[Issue]
)

case class AddPullRequestForm(
  summary: String,
  description: Option[String],
  base: String,
  branch: String,
  issueId: Option[Id[Issue]] = None,
  assigneeId: Option[Id[User]] = None,
  notifiedUserId: Seq[Id[User]] = Seq(),
  attachmentId: Seq[Id[Attachment]] = Seq()
)

case class UpdatePullRequestForm(
  summary: Option[String] = None,
  description: Option[String] = None,
  issueId: Option[Id[Issue]] = None,
  assigneeId: Option[Id[User]] = None,
  notifiedUserId: Option[Id[User]] = None,
  comment: Option[String] = None
)