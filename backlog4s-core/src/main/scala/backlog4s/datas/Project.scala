package backlog4s.datas

object ProjectT {
  def id(value: Long): Id[Project] = Id(value)
  def key(value: String): Key[Project] = Key(value)
}

case class Project(
  id: Id[Project],
  projectKey: Key[Project],
  name: String,
  chartEnabled: Boolean,
  subtaskingEnabled: Boolean,
  projectLeaderCanEditProjectLeader: Boolean,
  textFormattingRule: String,
  archived: Boolean
)

case class AddProjectForm(
  key: Key[Project],
  chartEnabled: Boolean,
  projectLeaderCanEditProjectLeader: Option[Boolean],
  subtaskingEnabled: Boolean,
  textFormattingRule: String
)

case class UpdateProjectForm(
  name: Option[String],
  key: Option[Key[Project]],
  chartEnabled: Option[Boolean],
  subtaskingEnabled: Option[Boolean],
  projectLeaderCanEditProjectLeader: Option[Boolean],
  textFormattingRule: Option[String],
  archived: Option[Boolean]
)