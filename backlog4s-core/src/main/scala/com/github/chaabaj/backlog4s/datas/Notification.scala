package com.github.backlog4s.datas

case class Notification(
  id: Id[Notification],
  alreadyRead: Boolean,
  reason: Int,
  user: User,
  resourceAlreadyRead: Boolean
)

case class AddNotificationForm(
  notifiedUserId: Seq[Id[User]]
)