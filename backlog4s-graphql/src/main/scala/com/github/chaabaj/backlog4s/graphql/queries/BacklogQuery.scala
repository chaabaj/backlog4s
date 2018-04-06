package com.github.chaabaj.backlog4s.graphql.queries

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.graphql.schemas.ProjectSchema.schema
import com.github.chaabaj.backlog4s.graphql.repositories.BacklogRepository
import com.github.chaabaj.backlog4s.graphql.schemas.IssueSchema
import sangria.marshalling.ToInput
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema.{Argument, Field, IntType, ListInputType, ListType, ObjectType, Schema, StringType, fields}

object BacklogQuery {

  implicit val listIntInput = new ScalarToInput[Seq[Int]]

  val ID = Argument("id", IntType)
  val Offset = Argument.createWithDefault("offset", IntType, None, 0)
  val Count = Argument.createWithDefault("count", IntType, None, 100)
  val ProjectIds = Argument.createWithDefault(
    "projectIds",
    ListInputType(IntType),
    Some("list of project id"),
    Seq.empty[Int]
  )
  val IssueTypeIds = Argument.createWithDefault(
    "issueTypeIds",
    ListInputType(IntType),
    Some("list of issue type id"),
    Seq.empty[Int]
  )
  val CategoryIds = Argument.createWithDefault(
    "categoryIds",
    ListInputType(IntType),
    Some("list of category id"),
    Seq.empty[Int]
  )
  val MilestoneIds = Argument.createWithDefault(
    "milestoneIds",
    ListInputType(IntType),
    Some("list of milestone id"),
    Seq.empty[Int]
  )

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
                projectIds = (ctx arg ProjectIds)
                  .map(_.asInstanceOf[Int])
                  .map(ProjectT.id),
                issueTypeIds = (ctx arg IssueTypeIds)
                  .map(_.asInstanceOf[Int])
                  .map(IssueTypeT.id),
                categoryIds = (ctx arg CategoryIds)
                  .map(_.asInstanceOf[Int])
                  .map(CategoryT.id),
                milestoneIds = (ctx arg MilestoneIds)
                    .map(_.asInstanceOf[Int])
                  .map(MilestoneT.id)
              )
            )
          )
        )
      )
    )
}
