package com.github.backlog4s.graphql.schemas

import com.github.backlog4s.datas.{ProjectDiskUsage, SpaceDiskUsage}
import com.github.backlog4s.graphql.repositories.BacklogRepository
import sangria.schema._

object DiskUsageSchema extends BacklogSchema[Unit, SpaceDiskUsage] {

  private val projectDiskUsage: ObjectType[BacklogRepository, ProjectDiskUsage] =
    ObjectType(
      "ProjectDiskUsage",
      "Project disk usage",
      () => fields[BacklogRepository, ProjectDiskUsage](
        Field(
          "project",
          ProjectSchema.schema,
          resolve = ctx =>
            ctx.ctx.fetchers.projects.defer(ctx.value.projectId.value)
        ),
        Field(
          "issue",
          LongType,
          resolve = _.value.issue
        ),
        Field(
          "wiki",
          LongType,
          resolve = _.value.wiki
        ),
        Field(
          "file",
          LongType,
          resolve = _.value.file
        ),
        Field(
          "subversion",
          LongType,
          resolve = _.value.subversion
        ),
        Field(
          "git",
          LongType,
          resolve = _.value.git
        )
      )
    )

  val schema: ObjectType[Unit, SpaceDiskUsage] =
    ObjectType(
      "DiskUsage",
      "Information about space disk usage",
      () => fields[Unit, SpaceDiskUsage](
        Field(
          "capacity",
          LongType,
          resolve = _.value.capacity
        ),
        Field(
          "issue",
          LongType,
          resolve = _.value.issue
        ),
        Field(
          "wiki",
          LongType,
          resolve = _.value.wiki
        ),
        Field(
          "file",
          LongType,
          resolve = _.value.file
        ),
        Field(
          "subversion",
          LongType,
          resolve = _.value.subversion
        ),
        Field(
          "git",
          LongType,
          resolve = _.value.git
        )
      )
    )
}
