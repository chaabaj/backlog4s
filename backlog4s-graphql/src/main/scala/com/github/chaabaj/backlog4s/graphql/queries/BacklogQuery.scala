package com.github.chaabaj.backlog4s.graphql.queries

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats.DateTimeFormat
import com.github.chaabaj.backlog4s.graphql.schemas.ProjectSchema.schema
import com.github.chaabaj.backlog4s.graphql.repositories.BacklogRepository
import com.github.chaabaj.backlog4s.graphql.schemas.IssueSchema
import org.joda.time.DateTime
import sangria.marshalling.ToInput.ScalarToInput
import sangria.schema.{Argument, Field, IntType, BooleanType, StringType,
  ListInputType, ListType, ObjectType, OptionInputType, Schema, fields}

object BacklogQuery {

  implicit val listIntInput = new ScalarToInput[Seq[Int]]

  private def parseDateTime(s: String): DateTime =
    DateTime.parse(s, DateTimeFormat.formatter)

  val ID = Argument("id", IntType)
  val Offset = Argument.createWithDefault("offset", OptionInputType(IntType), None, 0)
  val Count = Argument.createWithDefault("count", OptionInputType(IntType), None, 100)
  val ProjectIds = Argument.createWithDefault(
    "projectIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of project id"),
    Seq.empty[Int]
  )
  val IssueTypeIds = Argument.createWithDefault(
    "issueTypeIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of issue type id"),
    Seq.empty[Int]
  )
  val CategoryIds = Argument.createWithDefault(
    "categoryIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of category id"),
    Seq.empty[Int]
  )
  val MilestoneIds = Argument.createWithDefault(
    "milestoneIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of milestone id"),
    Seq.empty[Int]
  )
  val StatusIds = Argument.createWithDefault(
    "statusIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of statuses id"),
    Seq.empty[Int]
  )
  val PriorityIds = Argument.createWithDefault(
    "priorityIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of priority ids"),
    Seq.empty[Int]
  )
  val AssigneeIds = Argument.createWithDefault(
    "assigneeIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of assignee ids"),
    Seq.empty[Int]
  )
  val CreatedUserIds = Argument.createWithDefault(
    "createdUserIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of created user ids"),
    Seq.empty[Int]
  )
  val ResolutionIds = Argument.createWithDefault(
    "resolutionIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of resolution ids"),
    Seq.empty[Int]
  )
  val IssueIds = Argument.createWithDefault(
    "ids",
    OptionInputType(ListInputType(IntType)),
    Some("list of issues id"),
    Seq.empty[Int]
  )
  val ParentIssueIds = Argument.createWithDefault(
    "parentIssueIds",
    OptionInputType(ListInputType(IntType)),
    Some("list of issue parent ids"),
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
            arguments = Offset
              :: Count
              :: ProjectIds
              :: IssueIds
              :: IssueTypeIds
              :: CategoryIds
              :: MilestoneIds
              :: StatusIds
              :: PriorityIds
              :: AssigneeIds
              :: CreatedUserIds
              :: ResolutionIds
              :: IssueIds
              :: ParentIssueIds
              :: Argument("attachment", OptionInputType(BooleanType))
              :: Argument("sharedFile", OptionInputType(BooleanType))
              :: Argument("sort", OptionInputType(StringType))
              :: Argument("order", OptionInputType(StringType))
              :: Argument("createdSince", OptionInputType(StringType))
              :: Argument("createdUntil", OptionInputType(StringType))
              :: Argument("updatedSince", OptionInputType(StringType))
              :: Argument("updatedUntil", OptionInputType(StringType))
              :: Argument("startDateSince", OptionInputType(StringType))
              :: Argument("startDateUntil", OptionInputType(StringType))
              :: Argument("dueDateSince", OptionInputType(StringType))
              :: Argument("dueDateUntil", OptionInputType(StringType))
              :: Argument("keyword", OptionInputType(StringType))
              :: Nil,

            resolve = ctx => ctx.ctx.getIssues(
              IssueSearch(
                offset = ctx arg Offset,
                count = ctx arg Count,
                projectIds = (ctx arg ProjectIds)
                  .map(ProjectT.id),
                issueTypeIds = (ctx arg IssueTypeIds)
                  .map(IssueTypeT.id),
                categoryIds = (ctx arg CategoryIds)
                  .map(CategoryT.id),
                milestoneIds = (ctx arg MilestoneIds)
                  .map(MilestoneT.id),
                statusIds = (ctx arg StatusIds)
                  .map(StatusT.id),
                priorityIds = (ctx arg PriorityIds)
                  .map(PriorityT.id),
                assigneeIds = (ctx arg AssigneeIds)
                  .map(UserT.id),
                createdUserIds = (ctx arg CreatedUserIds)
                  .map(UserT.id),
                resolutionIds = (ctx arg ResolutionIds)
                  .map(ResolutionT.id),
                attachment = ctx argOpt[Boolean] "attachment",
                sharedFile = ctx argOpt[Boolean] "sharedFile",
                sort = (ctx argOpt[String] "sort").map(IssueSearchBy.withName),
                order = (ctx argOpt[String] "order").map(Order.withName).getOrElse(Order.Desc),
                createdSince = (ctx argOpt[String] "createdSince").map(parseDateTime),
                createdUntil = (ctx argOpt[String] "createdUntil").map(parseDateTime),
                updatedSince = (ctx argOpt[String] "updatedSince").map(parseDateTime),
                updatedUntil = (ctx argOpt[String] "updatedUntil").map(parseDateTime),
                startDateSince = (ctx argOpt[String] "startDateSince").map(parseDateTime),
                startDateUntil = (ctx argOpt[String] "startDateUntil").map(parseDateTime),
                dueDateSince = (ctx argOpt[String] "dueDateSince").map(parseDateTime),
                dueDateUntil = (ctx argOpt[String] "dueDateUntil").map(parseDateTime),
                ids = (ctx arg IssueIds)
                  .map(IssueT.id),
                parentIssueIds = (ctx arg ParentIssueIds)
                  .map(IssueT.id),
                keyword = ctx argOpt[String] "keyword"
              )
            )
          )
        )
      )
    )
}
