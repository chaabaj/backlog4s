package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas._
import sangria.schema._

object UserSchema extends BacklogSchema[Unit, User] {

  val schema: ObjectType[Unit, User] =
    ObjectType(
      "User",
      "Backlog user",
      () => fields[Unit, User](
        Field(
          "id",
          LongType,
          Some("user id"),
          resolve = _.value.id.value
        ),
        Field(
          "userId",
          OptionType(StringType),
          Some("Really this is his true id"),
          resolve = _.value.userId
        ),
        Field(
          "name",
          StringType,
          Some("user name"),
          resolve = _.value.name
        ),
        Field(
          "roleType",
          EnumType(
            "Role",
            values = List(
              EnumValue("Admin", value = Role.Admin.id),
              EnumValue("NormalUser", value = Role.NormalUser.id),
              EnumValue("Reporter", value = Role.Reporter.id),
              EnumValue("Viewer", value = Role.Viewer.id),
              EnumValue("GuestReporter", value = Role.GuestReporter.id),
              EnumValue("GuestViewer", value = Role.GuestViewer.id)
            )
          ),
          Some("user role"),
          resolve = _.value.roleType.id
        ),
        Field(
          "mailAddress",
          StringType,
          Some("mail address"),
          resolve = _.value.mailAddress
        ),
        Field(
          "lang",
          OptionType(
            EnumType(
            "Lang",
            values = List(
                EnumValue("English", value = Lang.English.toString),
                EnumValue("Japanese", value = Lang.Japanese.toString)
              )
            )
          ),
          Some("Language"),
          resolve = _.value.lang.toString
        )
      )
    )
}
