package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.{ByteStream, Response}
import com.github.chaabaj.backlog4s.dsl.HttpQuery
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class GitApi(override val baseUrl: String,
             override val credentials: Credentials) extends Api {

  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/git/repositories"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[GitRepository]]] =
    get[Seq[GitRepository]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def byIdOrName(projectIdOrKey: IdOrKeyParam[Project],
                 repoIdOrName: IdOrKeyParam[GitRepository]): ApiPrg[Response[GitRepository]] =
    get[GitRepository](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def pullRequests(projectIdOrKey: IdOrKeyParam[Project],
                   repoIdOrName: IdOrKeyParam[GitRepository]): ApiPrg[Response[Seq[PullRequestSummary]]] =
    get[Seq[PullRequestSummary]](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def countPullRequests(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository]): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/count",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def pullRequest(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long): ApiPrg[Response[PullRequest]] =
    get[PullRequest](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def createPullRequest(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository],
                        form: AddPullRequestForm): ApiPrg[Response[PullRequest]] =
    post[AddPullRequestForm, PullRequest](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def updatePullRequest(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository],
                        pullRequestNumber: Long,
                        form: UpdatePullRequestForm): ApiPrg[Response[PullRequest]] =
    put[UpdatePullRequestForm, PullRequest](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def attachments(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long): ApiPrg[Response[Seq[Attachment]]] =
    get[Seq[Attachment]](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadAttachment(projectIdOrKey: IdOrKeyParam[Project],
                         repoIdOrName: IdOrKeyParam[GitRepository],
                         pullRequestNumber: Long,
                         attachmentId: Id[Attachment]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def removeAttachment(projectIdOrKey: IdOrKeyParam[Project],
                       repoIdOrName: IdOrKeyParam[GitRepository],
                       pullRequestNumber: Long,
                       attachmentId: Id[Attachment]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

}

object GitApi extends ApiContext[GitApi] {
  override def apply(baseUrl: String, credentials: Credentials): GitApi =
    new GitApi(baseUrl, credentials)
}