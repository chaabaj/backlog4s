package backlog4s.graphql.schemas

import backlog4s.datas.Project
import backlog4s.graphql.repositories.BacklogRepository
import backlog4s.graphql.repositories.BacklogResolvers.DeferredIssues
import sangria.schema._

object ProjectSchema extends BacklogSchema[BacklogRepository, Project] {

  val schema: ObjectType[BacklogRepository, Project] =
    ObjectType(
      "Project",
      "Backlog project",
      () => fields[BacklogRepository, Project](
        Field(
          "id",
          IntType,
          Some("Project id"),
          resolve = _.value.id.value.toInt
        ),
        Field(
          "projectKey",
          StringType,
          Some("Project key"),
          resolve = _.value.projectKey.value
        ),
        Field(
          "name",
          StringType,
          Some("Name"),
          resolve = _.value.name
        ),
        Field(
          "chartEnabled",
          BooleanType,
          Some("Chart enabled"),
          resolve = _.value.chartEnabled
        ),
        Field(
          "subtaskingEnabled",
          BooleanType,
          Some("Subtasking enabled"),
          resolve = _.value.subtaskingEnabled
        ),
        Field(
          "projectLeaderCanEditProjectLeader",
          BooleanType,
          Some("Who know what it is"),
          resolve = _.value.projectLeaderCanEditProjectLeader
        ),
        Field(
          "textFormattingRule",
          StringType,
          Some("text formatting rule"),
          resolve = _.value.textFormattingRule
        ),
        Field(
          "archived",
          BooleanType,
          Some("Project is archived?"),
          resolve = _.value.archived
        ),
        Field(
          "issues",
          ListType(IssueSchema.schema),
          resolve = ctx => DeferredIssues(ctx.value.id)
        ),
        Field(
          "wikis",
          ListType(WikiSummarySchema.schema),
          resolve = ctx => ctx.ctx.getWikiSummaries(ctx.value.id)
        )
      )
    )
}