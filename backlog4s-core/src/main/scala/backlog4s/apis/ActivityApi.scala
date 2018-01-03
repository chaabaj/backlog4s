package backlog4s.apis

import backlog4s.datas.{Activity, Id, User}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object ActivityApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def user(id: Id[User]): ApiPrg[Response[Seq[Activity]]] =
    get[Seq[Activity]](
      HttpQuery(
        s"users/${id.value}/activities"
      )
    )

  def space: ApiPrg[Response[Seq[Activity]]] =
    get[Seq[Activity]](
      HttpQuery("space/activities")
    )
}
