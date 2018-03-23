package backlog4s.graphql.schemas

import backlog4s.datas.WikiSummary
import sangria.schema._

object WikiSummarySchema extends BacklogSchema[Unit, WikiSummary] {

  override def schema: ObjectType[Unit, WikiSummary] =
    ObjectType(
      "WikiSummary",
      "Wiki summary",
      () => fields[Unit, WikiSummary](
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
          "projectId",
          IntType,
          resolve = _.value.projectId.value.toInt
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
