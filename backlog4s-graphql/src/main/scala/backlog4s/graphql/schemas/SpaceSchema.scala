package backlog4s.graphql.schemas

import backlog4s.datas.{Lang, Space}
import backlog4s.graphql.repositories.BacklogRepository
import sangria.schema._

object SpaceSchema extends BacklogSchema[BacklogRepository, Space] {

  val schema: ObjectType[BacklogRepository, Space] =
    ObjectType(
      "Space",
      "Backlog space",
      () => fields[BacklogRepository, Space](
        Field(
          "spaceKey",
          StringType,
          resolve = _.value.spaceKey.value
        ),
        Field(
          "name",
          StringType,
          resolve = _.value.name
        ),
        Field(
          "owner",
          UserSchema.schema,
          resolve = ctx => ctx.ctx.getUser(ctx.value.ownerId.value)
        ),
        Field(
          "lang",
          StringType,
          resolve = _.value.lang.getOrElse(Lang.English).toString
        ),
        Field(
          "timezone",
          StringType,
          resolve = _.value.timezone.getId
        ),
        Field(
          "reportSendTime",
          StringType,
          resolve = _.value.reportSendTime.toString
        ),
        Field(
          "textFormattingRule",
          StringType,
          resolve = _.value.textFormattingRule
        ),
        Field(
          "created",
          StringType,
          resolve = _.value.created.toString
        ),
        Field(
          "updated",
          OptionType(StringType),
          resolve = _.value.updated.map(_.toString)
        ),
        Field(
          "projects",
          ListType(ProjectSchema.schema),
          resolve = ctx => ctx.ctx.getProjects
        )
      )
    )
}
