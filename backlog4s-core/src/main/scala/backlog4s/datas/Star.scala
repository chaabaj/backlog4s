package backlog4s.datas

import org.joda.time.DateTime

case class Star(
  id: Id[Star],
  comment: Option[String],
  url: String,
  title: String,
  presenter: User,
  created: DateTime
)

case class StarForm(
  issueId: Option[Id[Issue]] = None,
  commentId: Option[Id[Comment]] = None,
  wikiId: Option[Id[Wiki]] = None,
  pullRequestId: Option[Id[PullRequest]] = None,
  pullRequestCommentId: Option[Id[Comment]] = None
)
