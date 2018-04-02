package com.github.backlog4s.graphql.schemas

import com.github.backlog4s.datas.PullRequestSummary
import sangria.schema._

object PullRequestSummarySchema extends BacklogSchema[Unit, PullRequestSummary] {

  override def schema: ObjectType[Unit, PullRequestSummary] =
    ObjectType(
      "PullRequestSummary",
      "Pull request summary information",
      () => fields[Unit, PullRequestSummary](
        Field(
          "id",
          IntType,
          resolve = _.value.id.value.toInt
        ),
        Field(
          "projectId",
          IntType,
          resolve = _.value.projectId.value.toInt
        ),
        Field(
          "repositoryId",
          IntType,
          resolve = _.value.repositoryId.value.toInt
        ),
        Field(
          "number",
          IntType,
          resolve = _.value.number.toInt
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
          OptionType(IntType),
          resolve = _.value.issue.map(_.id.value.toInt)
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
          "createdUser",
          UserSchema.schema,
          resolve = _.value.createdUser
        ),
        Field(
          "created",
          OptionType(StringType),
          resolve = _.value.created.toString
        ),
        Field(
          "updatedUser",
          OptionType(UserSchema.schema),
          resolve = _.value.updatedUser
        ),
        Field(
          "updated",
          OptionType(StringType),
          resolve = _.value.updated.map(_.toString())
        )
      )
    )

}
