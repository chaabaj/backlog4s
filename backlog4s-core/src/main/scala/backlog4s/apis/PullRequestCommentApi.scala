package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object PullRequestCommentApi {

  import backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project],
               repoIdOrName: IdOrKeyParam[GitRepository],
               pullRequestNumber: Long): String =
    s"projects/$projectIdOrKey/git/repositories/$repoIdOrName/pullRequests/$pullRequestNumber/comments"

  def comments(projectIdOrKey: IdOrKeyParam[Project],
               repoIdOrName: IdOrKeyParam[GitRepository],
               pullRequestNumber: Long): ApiPrg[Response[Seq[Comment]]] =
    get[Seq[Comment]](
      HttpQuery(
        resource(projectIdOrKey, repoIdOrName, pullRequestNumber)
      )
    )

  def count(projectIdOrKey: IdOrKeyParam[Project],
            repoIdOrName: IdOrKeyParam[GitRepository],
            pullRequestNumber: Long): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(s"${resource(projectIdOrKey, repoIdOrName, pullRequestNumber)}/count")
    )

  def postComment(projectIdOrKey: IdOrKeyParam[Project],
                  repoIdOrName: IdOrKeyParam[GitRepository],
                  pullRequestNumber: Long,
                  form: AddCommentForm): ApiPrg[Response[Comment]] =
    post[AddCommentForm, Comment](
      HttpQuery(
        resource(projectIdOrKey, repoIdOrName, pullRequestNumber)
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
        s"${resource(projectIdOrKey, repoIdOrName, pullRequestNumber)}/${commentId.value}"
      ),
      Map(
        "content" -> newContent
      )
    )
}
