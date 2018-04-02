package com.github.backlog4s.graphql.queries

import com.github.backlog4s.datas.{IssueT, ProjectT}
import com.github.backlog4s.graphql.repositories.BacklogRepository
import com.github.backlog4s.graphql.schemas.IssueSchema
import com.github.backlog4s.graphql.schemas.ProjectSchema.schema
import sangria.schema.{Argument, Field, IntType, ListType, ObjectType, Schema, fields}

object BacklogQuery {

  val ID = Argument("id", IntType, description = "id of the project")

  val queries =
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
