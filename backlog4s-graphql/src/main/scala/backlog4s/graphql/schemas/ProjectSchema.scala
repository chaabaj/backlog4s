package backlog4s.graphql.schemas

import backlog4s.datas.{Issue, IssueT, Project, ProjectT}
import backlog4s.graphql.repositories.BacklogRepository
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
          resolve = ctx => ctx.ctx.getIssues(ctx.value.id)
        ),
        Field(
          "wikis",
          ListType(WikiSummarySchema.schema),
          resolve = ctx => ctx.ctx.getWikiSummaries(ctx.value.id)
        )
      )
    )

  val ID = Argument("id", IntType, description = "id of the project")

  val ProjectQuery =
    Schema(
      ObjectType(
        "Query", fields[BacklogRepository, Unit](
          Field(
            "project",
            schema,
            arguments = ID :: Nil,
            resolve = ctx => ctx.ctx.getProject(ProjectT.id(ctx arg ID))
          ),
          Field(
            "projects",
            ListType(schema),
            arguments = Nil,
            resolve = ctx => ctx.ctx.getProjects
          ),
          Field(
            "issue",
            IssueSchema.schema,
            arguments = ID :: Nil,
            resolve = ctx => ctx.ctx.getIssue(IssueT.id(ctx arg ID))
          )
        )
      )
    )
}