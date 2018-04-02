package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas._
import sangria.schema._

object MilestoneSchema extends BacklogSchema[Unit, Milestone]{

  val schema: ObjectType[Unit, Milestone] =
    ObjectType(
      "Milestone",
      "milestone",
      () => fields[Unit, Milestone](
        Field(
          "id",
          IntType,
          Some("id"),
          resolve = _.value.id.value.toInt
        ),
        Field(
          "projectId",
          IntType,
          Some("projectId"),
          resolve = _.value.projectId.value.toInt
        ),
        Field(
          "name",
          StringType,
          resolve = _.value.name
        ),
        Field(
          "description",
          OptionType(StringType),
          resolve = _.value.description
        ),
        Field(
          "startDate",
          OptionType(StringType),
          resolve = _.value.startDate.map(_.toString)
        ),
        Field(
          "releaseDueDate",
          OptionType(StringType),
          resolve = _.value.releaseDueDate.map(_.toString)
        ),
        Field(
          "archived",
          BooleanType,
          resolve = _.value.archived
        ),
        Field(
          "displayOrder",
          IntType,
          resolve = _.value.displayOrder.toInt
        )
      )
    )
}
