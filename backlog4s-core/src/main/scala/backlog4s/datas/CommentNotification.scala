package backlog4s.datas

case class CommentNotification(
  id: Id[Comment],
  alreadyRead: Boolean,
  reason: Int,
  user: User,
  resourceAlreadyRead: Boolean
)

case class AddCommentNotificationForm(
  notifiedUserId: Seq[Id[User]]
)