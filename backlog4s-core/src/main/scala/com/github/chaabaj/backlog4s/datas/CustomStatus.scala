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

sealed trait CustomStatusColor extends StatusColor

object CustomStatusColor {
  case object Color1 extends CustomStatusColor {
    val hex: String = "#ea2c00"
  }
  case object Color2 extends CustomStatusColor {
    val hex: String = "#e87758"
  }
  case object Color3 extends CustomStatusColor {
    val hex: String = "#e07b9a"
  }
  case object Color4 extends CustomStatusColor {
    val hex: String = "#868cb7"
  }
  case object Color5 extends CustomStatusColor {
    val hex: String = "#3b9dbd"
  }
  case object Color6 extends CustomStatusColor {
    val hex: String = "#4caf93"
  }
  case object Color7 extends CustomStatusColor {
    val hex: String = "#b0be3c"
  }
  case object Color8 extends CustomStatusColor {
    val hex: String = "#eda62a"
  }
  case object Color9 extends CustomStatusColor {
    val hex: String = "#f42858"
  }
  case object Color10 extends CustomStatusColor {
    val hex: String = "#393939"
  }

  val values: Seq[CustomStatusColor] = Seq(
    Color1, Color2, Color3, Color4, Color5, Color6, Color7, Color8, Color9, Color10
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