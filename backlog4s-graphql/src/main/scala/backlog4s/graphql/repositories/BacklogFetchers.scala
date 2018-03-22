package backlog4s.graphql.repositories

import backlog4s.datas.Project
import backlog4s.dsl.BacklogHttpInterpret
import sangria.execution.deferred.{Fetcher, HasId}

import scala.concurrent.Future

class BacklogFetchers(interpret: BacklogHttpInterpret[Future]) {

  implicit object ProjectHasId extends HasId[Project, Long] {
    override def id(project: Project): Long = project.id.value
  }

  val projects = Fetcher(
    (projectRepo: BacklogRepository, ids: Seq[Long]) =>
      projectRepo.getProjects(ids)
  )
}
