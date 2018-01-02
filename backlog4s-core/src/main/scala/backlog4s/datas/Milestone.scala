package backlog4s.datas

import org.joda.time.DateTime

object MilestoneT {
  def id(value: Long): Id[Milestone] = Id(value)
}

case class Milestone(
  id: Id[Milestone],
  projectId: Id[Project],
  name: String,
  description: String,
  startDate: Option[DateTime],
  releaseDueDate: Option[DateTime],
  archived: Boolean,
  displayOrder: Long
)

case class AddMilestoneForm(
  name: String,
  description: Option[String],
  startDate: Option[DateTime],
  releaseDueDate: Option[DateTime]
)

case class UpdateMilestoneForm(
  name: String,
  description: Option[String],
  startDate: Option[DateTime],
  releaseDueDate: Option[DateTime],
  archived: Option[Boolean]
)