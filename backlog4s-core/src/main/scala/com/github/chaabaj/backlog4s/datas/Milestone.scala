package com.github.chaabaj.backlog4s.datas

import org.joda.time.DateTime

object MilestoneT {
  def id(value: Int): Id[Milestone] = Id(value)
}

case class Milestone(
  id: Id[Milestone],
  projectId: Id[Project],
  name: String,
  description: Option[String],
  startDate: Option[DateTime],
  releaseDueDate: Option[DateTime],
  archived: Boolean,
  displayOrder: Int
)

case class AddMilestoneForm(
  name: String,
  description: Option[String] = None,
  startDate: Option[DateTime] = None,
  releaseDueDate: Option[DateTime] = None
)

case class UpdateMilestoneForm(
  name: String,
  description: Option[String] = None,
  startDate: Option[DateTime] = None,
  releaseDueDate: Option[DateTime] = None,
  archived: Option[Boolean] = None
)