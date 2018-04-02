package com.github.backlog4s.datas

import com.github.backlog4s.datas.ActivityType.ActivityType
import org.joda.time.DateTime

object ActivityType extends Enumeration {
  type ActivityType = Value

  val IssueCreated = Value(1)
  val IssueUpdated = Value(2)
  val IssueCommented = Value(3)
  val IssueDeleted = Value(4)
  val WikiCreated = Value(5)
  val WikiUpdated = Value(6)
  val WikiDeleted = Value(7)
  val FileAdded = Value(8)
  val FileUpdated = Value(9)
  val FileDeleted = Value(10)
  val SVNCommitted = Value(11)
  val GitPushed = Value(12)
  val GitRepositoryCreated = Value(13)
  val IssueMultiUpdated = Value(14)
  val ProjectUserAdded = Value(15)
  val ProjectUserDeleted = Value(16)
  val CommentNotificationAdded = Value(17)
  val PullRequestAdded = Value(18)
  val PullRequestUpdated = Value(19)
  val CommentAddedOnPullRequest = Value(20)
}

case class Activity(
  id: Id[Activity],
  project: Project,
  `type`: ActivityType,
  content: ActivityContent,
  notifications: Seq[Notification],
  createdUser: User,
  created: DateTime
)

case class ActivityContent(
  id: Option[Id[ActivityContent]],
  key_id: Option[Long],
  summary: Option[String],
  description: Option[String],
  comment: Option[ActivityComment],
  changes: Option[Seq[ActivityChange]]
)

case class ActivityComment(
  id: Id[ActivityComment],
  content: String
)

case class ActivityChange(
  field: String,
  new_value: String,
  old_value: String,
  `type`: Option[String]
)