package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas.PullRequest
import com.github.chaabaj.backlog4s.graphql.repositories.BacklogRepository
import sangria.schema._

object PullRequestSchema extends BacklogSchema[BacklogRepository, PullRequest] {

  override def schema: ObjectType[BacklogRepository, PullRequest] =
    ObjectType(
      "PullRequest",
      "Pull request",
      () => fields[BacklogRepository, PullRequest](
        Field(
          "id",
          IntType,
          resolve = _.value.id.value
        ),
        Field(
          "projectId",
          IntType,
          resolve = _.value.projectId.value
        ),
        Field(
          "repositoryId",
          IntType,
          resolve = _.value.repositoryId.value
        ),
        Field(
          "number",
          IntType,
          resolve = _.value.number
        ),
        Field(
          "summary",
          StringType,
          resolve = _.value.summary
        ),
        Field(
          "description",
          OptionType(StringType),
          resolve = _.value.description
        ),
        Field(
          "base",
          StringType,
          resolve = _.value.base
        ),
        Field(
          "branch",
          StringType,
          resolve = _.value.branch
        ),
        Field(
          "status",
          StringType,
          resolve = _.value.status.name.toString
        ),
        Field(
          "assignee",
          OptionType(UserSchema.schema),
          resolve = _.value.assignee
        ),
        Field(
          "issue",
          OptionType(IssueSchema.schema),
          resolve = _.value.issue
        ),
        Field(
          "baseCommit",
          StringType,
          resolve = _.value.baseCommit
        ),
        Field(
          "branchCommit",
          StringType,
          resolve = _.value.branchCommit
        ),
        Field(
          "closedAt",
          OptionType(StringType),
          resolve = _.value.closedAt.map(_.toString())
        ),
        Field(
          "mergedAt",
          OptionType(StringType),
          resolve = _.value.mergedAt.map(_.toString())
        ),
        Field(
          "created",
          StringType,
          resolve = _.value.created.toString()
        ),
        Field(
          "createdUser",
          UserSchema.schema,
          resolve = _.value.createdUser
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
          "attachments",
          ListType(AttachmentSchema.schema),
          resolve = _.value.attachments
        ),
        Field(
          "stars",
          ListType(StarSchema.schema),
          resolve = _.value.stars
        ),
        Field(
          "comments",
          ListType(CommentSchema.schema),
          resolve = ctx => ctx.ctx.getComments(
            ctx.value.projectId,
            ctx.value.repositoryId,
            ctx.value.number
          )
        )
      )
    )
}
