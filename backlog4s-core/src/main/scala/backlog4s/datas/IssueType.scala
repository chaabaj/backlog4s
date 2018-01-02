package backlog4s.datas

case class IssueType(
  id: Id[IssueType],
  projectId: Id[Project],
  name: String,
  color: RGBColor,
  displayOrder: Long
)

case class UpdateIssueTypeForm(
  name: Option[String],
  color: Option[RGBColor]
)
