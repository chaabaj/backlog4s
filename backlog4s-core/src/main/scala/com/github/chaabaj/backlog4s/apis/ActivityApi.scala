package com.github.backlog4s.apis

import com.github.backlog4s.datas._
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.HttpADT.Response
import com.github.backlog4s.dsl.HttpQuery
import com.github.backlog4s.formatters.SprayJsonFormats._

class ActivityApi(override val baseUrl: String,
                  override val credentials: Credentials) extends Api {
  import com.github.backlog4s.dsl.ApiDsl.HttpOp._

  def user(id: Id[User]): ApiPrg[Response[Seq[Activity]]] =
    get[Seq[Activity]](
      HttpQuery(
        path = s"users/${id.value}/activities",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def space: ApiPrg[Response[Seq[Activity]]] =
    get[Seq[Activity]](
      HttpQuery(
        path = "space/activities",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
object ActivityApi extends ApiContext[ActivityApi] {
  override def apply(baseUrl: String, credentials: Credentials): ActivityApi =
    new ActivityApi(baseUrl, credentials)
}
