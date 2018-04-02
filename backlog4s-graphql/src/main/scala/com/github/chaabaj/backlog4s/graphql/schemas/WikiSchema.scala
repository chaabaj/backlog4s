package com.github.backlog4s.graphql.schemas

import com.github.backlog4s.datas.Wiki
import com.github.backlog4s.graphql.repositories.BacklogRepository
import sangria.schema._

object WikiSchema extends BacklogSchema[BacklogRepository, Wiki] {
  val schema: ObjectType[BacklogRepository, Wiki] =
    ObjectType(
      "Wiki",
      "Wiki",
      () => fields[BacklogRepository, Wiki](
        Field(
          "id",
          IntType,
          resolve = _.value.id.value.toInt
        ),
        Field(
          "projectId",
          IntType,
          resolve = _.value.projectId.value.toInt
        ),
        Field(
          "project",
          ProjectSchema.schema,
          resolve = ctx =>
            ctx.ctx.fetchers.projects.defer(ctx.value.projectId.value)
        ),
        Field(
          "name",
          StringType,
          resolve = _.value.name
        ),
        Field(
          "tags",
          ListType(StringType),
          resolve = _.value.tags.map(_.name)
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
