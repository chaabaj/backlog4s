package backlog4s.graphql.repositories

import backlog4s.apis.AllApi
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.BacklogHttpInterpret
import backlog4s.dsl.syntax._
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.concurrent.Future

class BacklogRepository(interpret : BacklogHttpInterpret[Future], allApi: AllApi)
                       (implicit scheduler: Scheduler) {

  private val projectApi = allApi.projectApi
  private val issueApi = allApi.issueApi
  private val commentApi = allApi.issueCommentApi

  def getProject(id: Long): Future[Project] =
    interpret.run(
      projectApi.byIdOrKey(IdParam(ProjectT.id(id))).orFail
    )

  def getProjects: Future[Seq[Project]] =
    interpret.run(
      projectApi.all().orFail
    )

  def getProjects(ids: Seq[Long]): Future[Seq[Project]] =
    Observable.fromIterator(ids.toIterator)
      .mapFuture(getProject)
      .foldLeftL(IndexedSeq.empty[Project]) {
        case (acc, project) =>
          acc :+ project
      }.runAsync

  def getIssues(id: Long): Future[Seq[Issue]] =
    interpret.run(
      issueApi.search(
        IssueSearch(projectIds = Seq(ProjectT.id(id)))
      ).orFail
    )

  def getIssues(ids: Seq[Long]): Future[Seq[Issue]] =
    interpret.run(
      issueApi.search(
        IssueSearch(projectIds = ids.map(ProjectT.id))
      ).orFail
    )

  def getComments(issueId: Long): Future[Seq[Comment]] =
    interpret.run(
      commentApi.allOf(IdParam(IssueT.id(issueId)), count = 100).orFail
    )


  def getIssue(id: Long): Future[Issue] =
    interpret.run(
      issueApi.byIdOrKey(IdParam(IssueT.id(id))).orFail
    )

  def getUser(id: Long): Future[User] =
    interpret.run(
      allApi.userApi.byId(UserT.id(id)).orFail
    )

  val fetchers = new BacklogFetchers(interpret)
}
