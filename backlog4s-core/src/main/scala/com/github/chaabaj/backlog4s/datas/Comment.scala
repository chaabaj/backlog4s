package com.github.chaabaj.backlog4s.datas

import org.joda.time.DateTime

object CommentT {
  def id(value: Int): Id[Comment] = Id(value)
}

case class Comment(
  id: Id[Comment],
  content: String,
  //changeLog: Option[Seq[Map[String, String]]],
  createdUser: User,
  created: DateTime,
  updated: Option[DateTime],
  stars: Seq[Star],
  notifications: Seq[Notification]
)

case class AddCommentForm(
  content: String,
  notifiedUserId: Seq[Id[User]] = Seq(),
  attachmentId: Seq[Id[Attachment]] = Seq()
)