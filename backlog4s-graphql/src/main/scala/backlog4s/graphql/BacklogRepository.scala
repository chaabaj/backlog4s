package backlog4s.graphql

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.syntax._

class BacklogRepository {

  private val projectApi = Api.all.projectApi
  private val issueApi = Api.all.issueApi
  private val commentApi = Api.all.issueCommentApi

  def getProject(id: Long): ApiPrg[Project] =
    projectApi.byIdOrKey(IdParam(ProjectT.id(id))).orFail

  def getProjects(): ApiPrg[Seq[Project]] =
    projectApi.all().orFail

  def getIssues(id: Long): ApiPrg[Seq[Issue]] = {
    issueApi.search(
      IssueSearch(projectIds = Seq(ProjectT.id(id)))
    ).orFail
  }

  def getIssues(ids: Seq[Long]): ApiPrg[Seq[Issue]] = {
    issueApi.search(
      IssueSearch(projectIds = ids.map(ProjectT.id))
    ).orFail
  }

  def getComments(issueId: Long): ApiPrg[Seq[Comment]] =
    commentApi.allOf(IdParam(IssueT.id(issueId)), count = 100).orFail

  def getIssue(id: Long): ApiPrg[Issue] =
    issueApi.byIdOrKey(IdParam(IssueT.id(id))).orFail

}
