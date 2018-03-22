package backlog4s.graphql

import backlog4s.apis.AllApi
import backlog4s.datas.{Issue, Project}
import backlog4s.dsl.BacklogHttpInterpret
import sangria.execution.deferred._
import sangria.schema._

import scala.concurrent.Future
import backlog4s.dsl.syntax._
import backlog4s.graphql.repositories.BacklogRepository


/**
  * Defines a GraphQL schema for the current project
  */
class SchemaDefinition(interp: BacklogHttpInterpret[Future], allApi: AllApi) {
  /**
    * Resolves the lists of characters. These resolutions are batched and
    * cached for the duration of a query.
    */

  implicit object ProjectHasId extends HasId[Project, Long] {
    override def id(project: Project): Long = project.id.value
  }
  implicit object IssueHasId extends HasId[Issue, Long] {
    override def id(issue: Issue): Long = issue.id.value
  }

  val projects = Fetcher(
    (projectRepo: BacklogRepository, ids: Seq[Long]) =>
      interp.run(
        ids.map(projectRepo.getProject).parallel
      )
  )

  val issueSchema = new IssueSchema(interp, allApi)

  val ProjectType: ObjectType[BacklogRepository, Project] =
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
          ListType(issueSchema.schema),
          resolve = ctx => interp.run(ctx.ctx.getIssues(ctx.value.id.value))
        )
      )
    )

  val ID = Argument("id", IntType, description = "id of the project")

  val ProjectQuery = ObjectType(
    "Query", fields[BacklogRepository, Unit](
      Field(
        "project",
        ProjectType,
        arguments = ID :: Nil,
        resolve = ctx => interp.run(ctx.ctx.getProject(ctx arg ID))
      ),
      Field(
        "projects",
        ListType(ProjectType),
        arguments = Nil,
        resolve = ctx => interp.run(ctx.ctx.getProjects())
      ),
      Field(
        "issue",
        issueSchema.schema,
        arguments = ID :: Nil,
        resolve = ctx => interp.run(ctx.ctx.getIssue(ctx arg ID))
      )
    )
  )

  val ProjectSchema = Schema(ProjectQuery)
}