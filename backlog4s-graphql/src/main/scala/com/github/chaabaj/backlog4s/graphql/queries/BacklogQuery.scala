package com.github.chaabaj.backlog4s.graphql.queries

import com.github.chaabaj.backlog4s.datas.{IssueSearch, IssueT, IssueTypeT, ProjectT}
import com.github.chaabaj.backlog4s.graphql.schemas.ProjectSchema.schema
import com.github.chaabaj.backlog4s.graphql.repositories.BacklogRepository
import com.github.chaabaj.backlog4s.graphql.schemas.IssueSchema
import sangria.schema.{Argument, Field, IntType, ListInputType, ListType, ObjectType, Schema, StringType, fields}

object BacklogQuery {

  val ID = Argument("id", IntType)
  val Offset = Argument.createWithDefault("offset", IntType, None, 0)
  val Count = Argument.createWithDefault("count", IntType, None, 100)
  val projectIds = Argument("projectIds", ListInputType(IntType))
  val issueTypeIds = Argument("issueTypeIds", ListInputType(IntType), "id of the issue type")

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
          ),
          Field(
            "issues",
            ListType(IssueSchema.schema),
            arguments = Offset :: Count :: Nil,
            resolve = ctx => ctx.ctx.getIssues(
              IssueSearch(
                offset = ctx arg Offset,
                count = ctx arg Count,
                projectIds = (ctx arg projectIds)
                  .map(_.asInstanceOf[Int])
                  .map(ProjectT.id),
                issueTypeIds = (ctx arg issueTypeIds)
                  .map(_.asInstanceOf[Int])
                  .map(IssueTypeT.id)
              )
            )
          )
        )
      )
    )
}
