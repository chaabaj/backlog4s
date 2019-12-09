package com.github.chaabaj.backlog4s.datas

case class CustomStatus(
  id: Id[CustomStatus],
  projectId: Id[Project],
  name: String,
  color: StatusColor,
  displayOrder: Int
)

sealed trait StatusColor {
  val hex: String
}

object StatusColor {

  def from(colorStr: String): Option[StatusColor] =
    (DefaultStatusColor.values ++ CustomStatusColor.values)
      .find(_.hex == colorStr)
}

sealed trait DefaultStatusColor extends StatusColor

object DefaultStatusColor {
  case object Open extends DefaultStatusColor {
    val hex: String = "#ed8077"
  }
  case object InProgress extends DefaultStatusColor {
    val hex: String = "#4488c5"
  }
  case object Resolved extends DefaultStatusColor {
    val hex: String = "#5eb5a6"
  }
  case object Closed extends DefaultStatusColor {
    val hex: String = "#b0be3c"
  }

  val values: Seq[DefaultStatusColor] = Seq(
    Open, InProgress, Resolved, Closed
  )

  def from(colorStr: String): Option[DefaultStatusColor] =
    values.find(_.hex == colorStr)
}

case class CustomStatusColor private (hex: String) extends StatusColor

object CustomStatusColor {
  val color1: CustomStatusColor  = CustomStatusColor("#ea2c00")
  val color2: CustomStatusColor  = CustomStatusColor("#e87758")
  val color3: CustomStatusColor  = CustomStatusColor("#e07b9a")
  val color4: CustomStatusColor  = CustomStatusColor("#868cb7")
  val color5: CustomStatusColor  = CustomStatusColor("#3b9dbd")
  val color6: CustomStatusColor  = CustomStatusColor("#4caf93")
  val color7: CustomStatusColor  = CustomStatusColor("#b0be3c")
  val color8: CustomStatusColor  = CustomStatusColor("#eda62a")
  val color9: CustomStatusColor  = CustomStatusColor("#f42858")
  val color10: CustomStatusColor = CustomStatusColor("#393939")

  val values:  Seq[CustomStatusColor] = Seq(
    color1, color2, color3, color4, color5, color6, color7, color8, color9, color10
  )

  def from(colorStr: String): Option[CustomStatusColor] =
    values.find(_.hex == colorStr)

}

case class UpdateCustomStatusForm(
  name: Option[String] = None,
  color: Option[CustomStatusColor] = None
)

case class UpdateCustomStatusDisplayOrderForm(
  statusId: Seq[Id[CustomStatus]]
)

case class DeleteCustomStatusForm(
  substituteStatusId: Id[CustomStatus]
)