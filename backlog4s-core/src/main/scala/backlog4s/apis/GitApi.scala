package backlog4s.apis

import backlog4s.datas.{GitRepository, IdOrKeyParam, Project}
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


}
