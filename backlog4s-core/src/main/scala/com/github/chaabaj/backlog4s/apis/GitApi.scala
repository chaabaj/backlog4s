package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class GitApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/git/repositories"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Seq[GitRepository]]] =
    BacklogHttpDsl.get[Seq[GitRepository]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def byIdOrName(projectIdOrKey: IdOrKeyParam[Project],
                 repoIdOrName: IdOrKeyParam[GitRepository]): F[Response[GitRepository]] =
    BacklogHttpDsl.get[GitRepository](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def pullRequests(projectIdOrKey: IdOrKeyParam[Project],
                   repoIdOrName: IdOrKeyParam[GitRepository]): F[Response[Seq[PullRequestSummary]]] =
    BacklogHttpDsl.get[Seq[PullRequestSummary]](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def countPullRequests(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository]): F[Response[Count]] =
    BacklogHttpDsl.get[Count](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/count",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def pullRequest(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long): F[Response[PullRequest]] =
    BacklogHttpDsl.get[PullRequest](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def createPullRequest(projectIdOrKey: IdOrKeyParam[Project],
                        repoIdOrName: IdOrKeyParam[GitRepository],
                        form: AddPullRequestForm): F[Response[PullRequest]] =
    BacklogHttpDsl.post[AddPullRequestForm, PullRequest](
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
                        form: UpdatePullRequestForm): F[Response[PullRequest]] =
    BacklogHttpDsl.put[UpdatePullRequestForm, PullRequest](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def attachments(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long): F[Response[Seq[Attachment]]] =
    BacklogHttpDsl.get[Seq[Attachment]](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadAttachment(projectIdOrKey: IdOrKeyParam[Project],
                         repoIdOrName: IdOrKeyParam[GitRepository],
                         pullRequestNumber: Long,
                         attachmentId: Id[Attachment]): F[Response[ByteStream]] =
    BacklogHttpDsl.download(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def removeAttachment(projectIdOrKey: IdOrKeyParam[Project],
                       repoIdOrName: IdOrKeyParam[GitRepository],
                       pullRequestNumber: Long,
                       attachmentId: Id[Attachment]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/$repoIdOrName/pullRequests/$pullRequestNumber/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

}
