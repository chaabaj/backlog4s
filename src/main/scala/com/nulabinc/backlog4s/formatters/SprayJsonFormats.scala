package com.nulabinc.backlog4s.formatters

import com.nulabinc.backlog4s.datas._
import spray.json._

object SprayJsonFormats extends DefaultJsonProtocol with Formats[JsonFormat] {

  class EnumFormat[E <: Enumeration](enu: E, enumType: EnumType) extends RootJsonFormat[E#Value] {
    override def read(json: JsValue): E#Value = json match {
      case JsNumber(num) => enu.apply(num.toInt)
      case JsString(name) => enu.withName(name)
      case _ => throw DeserializationException(s"Unexpected input ${json.prettyPrint}")
    }

    override def write(obj: E#Value): JsValue = enumType match {
      case IntEnum => JsNumber(obj.id)
      case StringEnum => JsString(obj.toString)
    }
  }

  class IdFormat[A]() extends RootJsonFormat[Id[A]] {
    override def read(json: JsValue): Id[A] = json match {
      case JsNumber(idVal) => Id(idVal.toLong)
      case _ =>
        throw DeserializationException(s"Expected a js number got ${json.prettyPrint}")
    }

    override def write(obj: Id[A]): JsValue = JsNumber(obj.value)
  }

  implicit val userIdFormat = new IdFormat[User]
  implicit val roleFormat = new EnumFormat(Role, IntEnum)
  implicit val langFormat = new EnumFormat(Lang, StringEnum)
  implicit val errorCodeFormat = new EnumFormat(ApiErrorCode, IntEnum)
  implicit val errorFormat = jsonFormat3(ApiError)
  implicit val errorsFormat = jsonFormat1(ApiErrors)
  implicit val userFormat: JsonFormat[User] = jsonFormat6(User)
  implicit val addUserFormFormat = jsonFormat5(AddUserForm)
}
