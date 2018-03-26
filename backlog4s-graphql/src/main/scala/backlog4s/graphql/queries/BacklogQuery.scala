package backlog4s.graphql.queries

import backlog4s.datas.{IssueT, ProjectT}
import backlog4s.graphql.repositories.BacklogRepository
import backlog4s.graphql.schemas.IssueSchema
import backlog4s.graphql.schemas.ProjectSchema.schema
import sangria.schema.{Argument, Field, IntType, ListType, ObjectType, Schema, fields}

object BacklogQuery {

  val ID = Argument("id", IntType, description = "id of the project")

  val Query =
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
