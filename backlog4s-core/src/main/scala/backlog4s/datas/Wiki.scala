package backlog4s.datas

import org.joda.time.DateTime

object WikiT {
  def id(value: Long): Id[WikiSummary] = Id(value)
}

case class WikiSummary(
  id: Id[Wiki],
  projectId: Id[Project],
  name: String,
  tags: Seq[WikiTag],
  createdUser: User,
  created: DateTime,
  updatedUser: Option[User],
  updated: Option[DateTime]
)

case class Wiki(
  id: Id[Wiki],
  projectId: Id[Project],
  name: String,
  content: String,
  tags: Seq[WikiTag],
  attachments: Seq[Attachment],
  sharedFiles: Seq[SharedFile],
  stars: Seq[Star],
  createdUser: User,
  created: DateTime,
  updatedUser: Option[User],
  updated: Option[DateTime]
)

case class WikiTag(
  id: Id[WikiTag],
  name: String
)

case class WikiHistory(
  pageId: Id[Wiki],
  version: Long,
  name: String,
  content: String,
  createdUser: User,
  created: DateTime
)

case class AddWikiForm(
  projectId: Id[Project],
  name: String,
  content: String,
  mailNotify: Option[Boolean]
)

case class UpdateWikiForm(
  name: Option[String],
  content: Option[String],
  mailNotify: Option[Boolean]
)