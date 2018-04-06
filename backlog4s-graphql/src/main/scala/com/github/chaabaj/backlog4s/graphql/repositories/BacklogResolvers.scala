package com.github.chaabaj.backlog4s.graphql.repositories

import com.github.chaabaj.backlog4s.datas.{Id, Issue, IssueSearch, Project}
import sangria.execution.deferred.{Deferred, DeferredResolver}

import scala.concurrent.{ExecutionContext, Future}

object BacklogResolvers {

  case class DeferredIssues(projectId: Id[Project]) extends Deferred[Seq[Issue]]

  case class IssueResolver() extends DeferredResolver[BacklogRepository] {

    private def distributeIssues(projectId: Id[Project], issues: IndexedSeq[Issue]): IndexedSeq[Issue] =
      issues.filter(_.projectId == projectId)


    override def resolve(deferred: Vector[Deferred[Any]], ctx: BacklogRepository, queryState: Any)(implicit ec: ExecutionContext): Vector[Future[Any]] = {
      val projectIds = deferred.map {
        case (DeferredIssues(id)) => id
      }

      val request = ctx.getIssues(IssueSearch(projectIds = projectIds))
      projectIds.map { id =>
        request.map { issues =>
          distributeIssues(id, issues.toIndexedSeq)
        }
      }
    }
  }
}
