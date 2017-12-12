package com.nulabinc.backlog4s.datas

import com.nulabinc.backlog4s.datas.Lang.Lang
import com.nulabinc.backlog4s.datas.Role.Role


object Role extends Enumeration {
  type Role = Value

  val Admin = Value(1)
  val NormalUser = Value(2)
  val Reporter = Value(3)
  val Viewer = Value(4)
  val GuestReporter = Value(5)
  val GuestViewer = Value(6)
}

object Lang extends Enumeration {
  type Lang = Value

  val English = Value("en")
  val Japanese = Value("ja")
}

case class User (
  id: Id[User],
  userId: Option[String],
  name: String,
  roleType: Role,
  mailAddress: String,
  lang: Option[Lang] = Some(Lang.English)
)

object UserT {
  def id(value: Long): Id[User] = Id(value)
  val myself: Id[User] = Id(0)
}