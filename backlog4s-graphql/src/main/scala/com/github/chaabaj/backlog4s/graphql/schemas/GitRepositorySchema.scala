package com.github.chaabaj.backlog4s.graphql.schemas

import com.github.chaabaj.backlog4s.datas.GitRepository
import com.github.chaabaj.backlog4s.graphql.repositories.BacklogRepository
import com.github.chaabaj.backlog4s.graphql.repositories.BacklogRepository
import sangria.schema._

object GitRepositorySchema
  extends BacklogSchema[BacklogRepository, GitRepository] {

  override def schema: ObjectType[BacklogRepository, GitRepository] =
    ObjectType(
      "GitRepository",
      "Git repository",
      () => fields[BacklogRepository, GitRepository](
        Field(
          "id",
          IntType,
          resolve = _.value.id.value
        ),
        Field(
          "projectId",
          IntType,
          resolve = _.value.id.value
        ),
        Field(
          "name",
          StringType,
          resolve = _.value.name
        ),
        Field(
          "description",
          OptionType(StringType),
          resolve = _.value.description
        ),
        Field(
          "hookUrl",
          OptionType(StringType),
          resolve = _.value.hookUrl
        ),
        Field(
          "httpUrl",
          OptionType(StringType),
          resolve = _.value.httpUrl
        ),
        Field(
          "sshUrl",
          OptionType(StringType),
          resolve = _.value.sshUrl
        ),
        Field(
          "pushedAt",
          OptionType(StringType),
          resolve = _.value.pushedAt
        ),
        Field(
          "displayOrder",
          IntType,
          resolve = _.value.displayOrder.toInt
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
          OptionType(StringType),
          resolve = _.value.updated.map(_.toString())
        ),
        Field(
          "pullRequests",
          ListType(PullRequestSummarySchema.schema),
          resolve = ctx => ctx.ctx.getPullRequestSummaries(ctx.value.projectId, ctx.value.id)
        )
      )
    )

}
