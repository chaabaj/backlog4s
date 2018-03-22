package backlog4s.graphql.schemas

import backlog4s.datas._
import backlog4s.graphql.repositories.BacklogRepository
import sangria.schema._

object IssueSchema extends BacklogSchema[Unit, IssueType] {

  val issueType: ObjectType[Unit, IssueType] =
    ObjectType(
      "IssueType",
      "Issue type",
      () => fields[Unit, IssueType](
        Field(
          "id",
          IntType,
          Some("issue type id"),
          resolve = _.value.id.value.toInt
        ),
        Field(
          "projectId",
          IntType,
          Some("Project id"),
          resolve = _.value.projectId.value.toInt
        ),
        Field(
          "name",
          StringType,
          Some("Issue type name"),
          resolve = _.value.name
        ),
        Field(
          "color",
          StringType,
          Some("issue oolor"),
          resolve = _.value.color.toHex
        ),
        Field(
          "displayOrder",
          IntType,
          Some("Display order"),
          resolve = _.value.displayOrder.toInt
        )
      )
    )

  val schema: ObjectType[BacklogRepository, Issue] =
    ObjectType(
      "Issue",
      "Backlog issue",
      () => fields[BacklogRepository, Issue](
        Field(
          "id",
          IntType,
          Some("Issue id"),
          resolve = _.value.id.value.toInt
        ),
        Field(
          "projectId",
          IntType,
          Some("Project id"),
          resolve = _.value.projectId.value.toInt
        ),
        Field(
          "keyId",
          IntType,
          Some("issue key"),
          resolve = _.value.keyId.toInt
        ),
        Field(
          "issueType",
          issueType,
          Some("issue type"),
          resolve = _.value.issueType
        ),
        Field(
          "summary",
          StringType,
          Some("issue summary"),
          resolve = _.value.summary
        ),
        Field(
          "description",
          StringType,
          Some("issue description"),
          resolve = _.value.description
        ),
        Field(
          "resolution",
          OptionType(StringType),
          Some("issue resolution"),
          resolve = _.value.resolution.map(_.name)
        ),
        Field(
          "priority",
          StringType,
          Some("priority"),
          resolve = _.value.priority.name
        ),
        Field(
          "status",
          StringType,
          Some("status"),
          resolve = _.value.status.name
        ),
        Field(
          "assignee",
          OptionType(UserSchema.schema),
          Some("assignee"),
          resolve = _.value.assignee
        ),
        Field(
          "categories",
          ListType(StringType),
          Some("categories"),
          resolve = _.value.category.map(_.name)
        ),
        Field(
          "milestone",
          ListType(MilestoneSchema.schema),
          Some("milestones"),
          resolve = _.value.milestone
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
          StringType,
          resolve = _.value.updated.toString
        ),
        Field(
          "comment",
          ListType(CommentSchema.schema),
          resolve = ctx => ctx.ctx.getComments(ctx.value.id.value)
        )
      )
    )
}
