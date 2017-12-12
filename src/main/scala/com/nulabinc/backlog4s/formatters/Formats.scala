package com.nulabinc.backlog4s.formatters

import com.nulabinc.backlog4s.datas.Lang.Lang
import com.nulabinc.backlog4s.datas.Role.Role
import com.nulabinc.backlog4s.datas.{Id, Lang, Role, User}

sealed trait EnumType
case object IntEnum extends EnumType
case object StringEnum extends EnumType

trait Formats[DataFormat[_]] {
  implicit def userIdFormat: DataFormat[Id[User]]
  implicit def roleFormat: DataFormat[Role]
  implicit def langFormat: DataFormat[Lang]
  implicit def userFormat: DataFormat[User]
}
