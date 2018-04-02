package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas.SharedFile
import sangria.schema._

object SharedFileSchema extends BacklogSchema[Unit, SharedFile] {

  override def schema: ObjectType[Unit, SharedFile] =
    ObjectType(
      "SharedFile",
      "Shared file",
      () => fields[Unit, SharedFile](
        Field(
          "id",
          IntType,
          resolve = _.value.id.value.toInt
        ),
        Field(
          "type",
          StringType,
          resolve = _.value.`type`
        ),
        Field(
          "dir",
          StringType,
          resolve = _.value.dir
        ),
        Field(
          "name",
          StringType,
          resolve = _.value.name
        ),
        Field(
          "size",
          IntType,
          resolve = _.value.size.toInt
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
          resolve = _.value.updated.map(_.toString)
        )
      )
    )

}
