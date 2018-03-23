package backlog4s.graphql.repositories

import backlog4s.apis.AllApi
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.BacklogHttpInterpret
import backlog4s.dsl.syntax._
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.concurrent.Future

class BacklogRepository(interpret : BacklogHttpInterpret[Future], allApi: AllApi, parallelism: Int = 4)
                       (implicit scheduler: Scheduler) {

  private val projectApi = allApi.projectApi
  private val issueApi = allApi.issueApi
  private val commentApi = allApi.issueCommentApi

  private def getAll[A](ids: Seq[Id[A]], f: Id[A] => ApiPrg[A]): Future[Seq[A]] = {
    val grouped = ids.grouped(parallelism)
    Observable.fromIterator(grouped)
      .mapFuture { chunkIds =>
        interpret.run(
          chunkIds.map(f).parallel
        )
      }
      .foldLeftL(IndexedSeq.empty[A]) {
        case (acc, items) =>
          acc ++ items
      }
      .runAsync
  }

  def getProject(id: Id[Project]): Future[Project] =
    interpret.run(
      projectApi.byIdOrKey(IdParam(id)).orFail
    )

  def getProjects: Future[Seq[Project]] =
    interpret.run(
      projectApi.all().orFail
    )

  def getProjects(ids: Seq[Id[Project]]): Future[Seq[Project]] =
    getAll[Project](ids, id => projectApi.byIdOrKey(IdParam(id)).orFail)

  def getIssues(id: Id[Project]): Future[Seq[Issue]] =
    interpret.run(
      issueApi.search(
        IssueSearch(projectIds = Seq(id))
      ).orFail
    )

  def getIssues(ids: Seq[Id[Project]]): Future[Seq[Issue]] =
    interpret.run(
      issueApi.search(
        IssueSearch(projectIds = ids)
      ).orFail
    )

  def getComments(issueId: Id[Issue]): Future[Seq[Comment]] =
    interpret.run(
      commentApi.allOf(IdParam(issueId), count = 100).orFail
    )


  def getIssue(id: Id[Issue]): Future[Issue] =
    interpret.run(
      issueApi.byIdOrKey(IdParam(id)).orFail
    )

  def getUser(id: Id[User]): Future[User] =
    interpret.run(
      allApi.userApi.byId(id).orFail
    )

  def getWiki(id: Id[Wiki]): Future[Wiki] =
    interpret.run(
      allApi.wikiApi.byId(id).orFail
    )

  def getWikis(ids: Seq[Id[Wiki]]): Future[Seq[Wiki]] =
    getAll[Wiki](ids, id => allApi.wikiApi.byId(id).orFail)

  def getWikiSummaries(id: Id[Project]): Future[Seq[WikiSummary]] =
    interpret.run(
      allApi.wikiApi.allOf(IdParam(id)).orFail
    )

  def getPullRequestSummaries(projectId: Id[Project],
                              id: Id[GitRepository]): Future[Seq[PullRequestSummary]] =
    interpret.run(
      allApi.gitApi.pullRequests(IdParam(projectId), IdParam(id)).orFail
    )

  val fetchers = new BacklogFetchers(interpret)
}
