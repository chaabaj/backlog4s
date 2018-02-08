package backlog4s.graphql

import backlog4s.datas.{IdParam, Project, ProjectT}
import backlog4s.dsl.BacklogHttpInterpret

import scala.concurrent.Future
import backlog4s.dsl.syntax._

class ProjectRepository(interp: BacklogHttpInterpret[Future]) {

  val projectApi = Api.all.projectApi

  def getProject(id: Int): Future[Project] =
    interp.run(
      projectApi.byIdOrKey(IdParam(ProjectT.id(id))).orFail
    )
}
