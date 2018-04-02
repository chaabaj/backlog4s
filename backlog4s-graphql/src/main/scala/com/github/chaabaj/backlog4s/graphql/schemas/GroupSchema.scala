package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas.Group
import com.github.chaabaj.backlog4s.graphql.repositories.BacklogRepository
import sangria.schema._

object GroupSchema extends BacklogSchema[Unit, Group] {

  override def schema: ObjectType[Unit, Group] =
    ObjectType(
      "Group",
      "User Group",
      () => fields[Unit, Group](
        Field(
          "id",
          IntType,
          resolve = _.value.id.value.toInt
        ),
        Field(
          "name",
          StringType,
          resolve = _.value.name
        ),
        Field(
          "members",
          ListType(UserSchema.schema),
          resolve = _.value.members
        ),
        Field(
          "displayOrder",
          OptionType(StringType),
          resolve = _.value.displayOrder.map(_.toString)
        ),
        Field(
          "createdUser",
          UserSchema.schema,
          resolve = _.value.createdUser
        ),
        Field(
          "created",
          StringType,
          resolve = _.value.created.toString()
        ),
        Field(
          "updatedUser",
          OptionType(UserSchema.schema),
          resolve = _.value.updatedUser
        ),
        Field(
          "updated",
          OptionType(StringType),
          resolve = _.value.updated.map(_.toString())
        )
      )
    )
}
