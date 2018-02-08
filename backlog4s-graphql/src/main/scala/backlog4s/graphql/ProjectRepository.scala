package backlog4s.graphql

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.syntax._

class ProjectRepository {

  private val projectApi = Api.all.projectApi
  private val issueApi = Api.all.issueApi

  def getProject(id: Long): ApiPrg[Project] =
    projectApi.byIdOrKey(IdParam(ProjectT.id(id))).orFail

  def getProjects(): ApiPrg[Seq[Project]] =
    projectApi.all().orFail

  def getIssues(id: Long): ApiPrg[Seq[Issue]] = {
    issueApi.search(
      IssueSearch(projectIds = Seq(ProjectT.id(id)))
    ).orFail
  }

}
