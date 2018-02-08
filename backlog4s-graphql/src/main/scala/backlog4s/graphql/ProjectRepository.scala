package backlog4s.graphql

import backlog4s.datas.{IdParam, Project, ProjectT}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.syntax._

class ProjectRepository {

  val projectApi = Api.all.projectApi

  def getProject(id: Long): ApiPrg[Project] =
    projectApi.byIdOrKey(IdParam(ProjectT.id(id))).orFail

  def getProjects(): ApiPrg[Seq[Project]] =
    projectApi.all().orFail
}
