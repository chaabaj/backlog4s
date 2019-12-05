package com.github.chaabaj.backlog4s.datas

case class CustomStatus(
  id: Id[CustomStatus],
  projectId: Id[Project],
  name: String,
  color: RGBColor,
  displayOrder: Int
)

case class UpdateCustomStatusForm(
  name: Option[String] = None,
  color: Option[RGBColor] = None
)

case class UpdateCustomStatusDisplayOrderForm(
  statusId: Seq[Id[CustomStatus]]
)