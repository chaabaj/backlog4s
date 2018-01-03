package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object GitApi {

  import backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/git/repositories"

  def getAll(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[GitRepository]]] =
    get[Seq[GitRepository]](
      HttpQuery(resource(projectIdOrKey))
    )

  def getById(projectIdOrKey: IdOrKeyParam[Project],
              repoIdOrName: IdOrKeyParam[GitRepository]): ApiPrg[Response[GitRepository]] =
    get[GitRepository](
      HttpQuery(
        s"${resource(projectIdOrKey)}/$repoIdOrName"
      )
    )

  def pullRequests(projectIdOrKey: IdOrKeyParam[Project],
                   repoIdOrName: IdOrKeyParam[GitRepository]): ApiPrg[Response[Seq[PullRequestSummary]]] =
    get[Seq[PullRequestSummary]](
      HttpQuery(
        s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests"
      )
    )

  def countPullRequests(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository]): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(
        s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/count"
      )
    )

  def pullRequest(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long): ApiPrg[Response[PullRequest]] =
    get[PullRequest](
      HttpQuery(
        s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber"
      )
    )

  def createPullRequest(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository],
                        form: AddPullRequestForm): ApiPrg[Response[PullRequest]] =
    post[AddPullRequestForm, PullRequest](
      HttpQuery(
        s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests"
      ),
      form
    )

  def updatePullRequest(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository],
                        pullRequestNumber: Long,
                        form: UpdatePullRequestForm): ApiPrg[Response[PullRequest]] =
    put[UpdatePullRequestForm, PullRequest](
      HttpQuery(
        s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber"
      ),
      form
    )
}
