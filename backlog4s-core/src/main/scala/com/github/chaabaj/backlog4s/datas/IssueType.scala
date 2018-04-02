package com.github.chaabaj.backlog4s.datas

case class IssueType(
  id: Id[IssueType],
  projectId: Id[Project],
  name: String,
  color: RGBColor,
  displayOrder: Long
)

case class UpdateIssueTypeForm(
  name: Option[String] = None,
  color: Option[RGBColor] = None
)
