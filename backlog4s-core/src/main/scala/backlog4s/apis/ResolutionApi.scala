package backlog4s.apis

import backlog4s.datas.Resolution
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object ResolutionApi {

  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "resolutions"

  def all: ApiPrg[Response[Seq[Resolution]]] =
    get[Seq[Resolution]](HttpQuery(resource))
}