package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas.Star
import sangria.schema._

object StarSchema extends BacklogSchema[Unit, Star] {

  override def schema: ObjectType[Unit, Star] =
    ObjectType(
      "Star",
      "Backlog Star",
      () => fields[Unit, Star](
        Field(
          "id",
          IntType,
          resolve = _.value.id.value
        ),
        Field(
          "comment",
          OptionType(StringType),
          resolve = _.value.comment
        ),
        Field(
          "url",
          StringType,
          resolve = _.value.url
        ),
        Field(
          "title",
          StringType,
          resolve = _.value.title
        ),
        Field(
          "presenter",
          UserSchema.schema,
          resolve = _.value.presenter
        ),
        Field(
          "created",
          StringType,
          resolve = _.value.created.toString()
        )
      )
    )
}
