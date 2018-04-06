package com.github.chaabaj.backlog4s.datas

import com.github.chaabaj.backlog4s.datas.Order.Order
import org.joda.time.DateTime

object GroupT {
  def id(value: Int): Id[Group] = Id(value)
}

case class Group(
  id: Id[Group],
  name: String,
  members: Seq[User],
  displayOrder: Option[Order],
  createdUser: User,
  created: DateTime,
  updatedUser: Option[User],
  updated: Option[DateTime]
)

case class AddGroupForm(
  members: Seq[User]
)

case class UpdateGroupForm(
  name: Option[String] = None,
  members: Option[Seq[User]] = None
)