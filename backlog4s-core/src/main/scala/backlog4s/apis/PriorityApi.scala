package backlog4s.apis

import backlog4s.datas.{Priority, Resolution}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object PriorityApi {

  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "priorities"

  def all: ApiPrg[Response[Seq[Priority]]] =
    get[Seq[Priority]](HttpQuery(resource))
}