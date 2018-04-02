package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas.Attachment
import sangria.schema._

object AttachmentSchema extends BacklogSchema[Unit, Attachment] {

  override def schema: ObjectType[Unit, Attachment] =
    ObjectType(
      "Attachment",
      "Attachment",
      () => fields[Unit, Attachment](
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
          "size",
          IntType,
          resolve = _.value.size.toInt
        )
      )
    )

}
