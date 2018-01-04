package backlog4s.apis

import backlog4s.datas.Status
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object StatusApi {

  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "statuses"

  def all: ApiPrg[Response[Seq[Status]]] =
    get[Seq[Status]](HttpQuery(resource))
}
