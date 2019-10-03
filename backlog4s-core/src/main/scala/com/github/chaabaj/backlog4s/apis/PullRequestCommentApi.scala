package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class PullRequestCommentApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def resource(projectIdOrKey: IdOrKeyParam[Project],
               repoIdOrName: IdOrKeyParam[GitRepository],
               pullRequestNumber: Long): String =
    s"projects/$projectIdOrKey/git/repositories/$repoIdOrName/pullRequests/$pullRequestNumber/comments"

  def comments(projectIdOrKey: IdOrKeyParam[Project],
               repoIdOrName: IdOrKeyParam[GitRepository],
               pullRequestNumber: Long): F[Response[Seq[Comment]]] =
    BacklogHttpDsl.get[Seq[Comment]](
      HttpQuery(
        path = resource(projectIdOrKey, repoIdOrName, pullRequestNumber),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def count(projectIdOrKey: IdOrKeyParam[Project],
            repoIdOrName: IdOrKeyParam[GitRepository],
            pullRequestNumber: Long): F[Response[Count]] =
    BacklogHttpDsl.get[Count](
      HttpQuery(
        path = s"${resource(projectIdOrKey, repoIdOrName, pullRequestNumber)}/count",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def postComment(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long,
                  form: AddCommentForm): F[Response[Comment]] =
    BacklogHttpDsl.post[AddCommentForm, Comment](
      HttpQuery(
        path = resource(projectIdOrKey, repoIdOrName, pullRequestNumber),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def editComment(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long,
                  commentId: Id[Comment],
                  newContent: String): F[Response[Comment]] =
    BacklogHttpDsl.put[CustomForm, Comment](
      HttpQuery(
        path = s"${resource(projectIdOrKey, repoIdOrName, pullRequestNumber)}/${commentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "content" -> newContent
      )
    )
}
