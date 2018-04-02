package com.github.backlog4s.apis

import com.github.backlog4s.datas.CustomForm.CustomForm
import com.github.backlog4s.datas._
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.HttpADT.Response
import com.github.backlog4s.dsl.HttpQuery
import com.github.backlog4s.formatters.SprayJsonFormats._

class PullRequestCommentApi(override val baseUrl: String,
                            override val credentials: Credentials) extends Api {

  import com.github.backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project],
               repoIdOrName: IdOrKeyParam[GitRepository],
               pullRequestNumber: Long): String =
    s"projects/$projectIdOrKey/git/repositories/$repoIdOrName/pullRequests/$pullRequestNumber/comments"

  def comments(projectIdOrKey: IdOrKeyParam[Project],
               repoIdOrName: IdOrKeyParam[GitRepository],
               pullRequestNumber: Long): ApiPrg[Response[Seq[Comment]]] =
    get[Seq[Comment]](
      HttpQuery(
        path = resource(projectIdOrKey, repoIdOrName, pullRequestNumber),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def count(projectIdOrKey: IdOrKeyParam[Project],
            repoIdOrName: IdOrKeyParam[GitRepository],
            pullRequestNumber: Long): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(
        path = s"${resource(projectIdOrKey, repoIdOrName, pullRequestNumber)}/count",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def postComment(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long,
                  form: AddCommentForm): ApiPrg[Response[Comment]] =
    post[AddCommentForm, Comment](
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
                  newContent: String): ApiPrg[Response[Comment]] =
    put[CustomForm, Comment](
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

object PullRequestCommentApi extends ApiContext[PullRequestCommentApi] {
  override def apply(baseUrl: String, credentials: Credentials): PullRequestCommentApi =
    new PullRequestCommentApi(baseUrl, credentials)
}
