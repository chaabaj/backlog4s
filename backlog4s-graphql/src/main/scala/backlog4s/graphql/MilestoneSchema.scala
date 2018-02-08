package backlog4s.graphql

import backlog4s.datas._
import sangria.schema._

object MilestoneSchema {

  val schema: ObjectType[Unit, Milestone] =
    ObjectType(
      "Milestone",
      "milestone",
      () => fields[Unit, Milestone](
        Field(
          "id",
          LongType,
          Some("id"),
          resolve = _.value.id.value
        ),
        Field(
          "projectId",
          LongType,
          Some("projectId"),
          resolve = _.value.projectId.value
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
          LongType,
          resolve = _.value.displayOrder
        )
      )
    )
}
