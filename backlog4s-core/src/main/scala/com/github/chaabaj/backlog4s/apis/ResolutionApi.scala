package com.github.backlog4s.apis

import com.github.backlog4s.datas.{Credentials, Resolution}
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.HttpADT.Response
import com.github.backlog4s.dsl.HttpQuery
import com.github.backlog4s.formatters.SprayJsonFormats._

class ResolutionApi(override val baseUrl: String,
                    override val credentials: Credentials) extends Api {

  import com.github.backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "resolutions"

  def all: ApiPrg[Response[Seq[Resolution]]] =
    get[Seq[Resolution]](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object ResolutionApi extends ApiContext[ResolutionApi] {
  override def apply(baseUrl: String, credentials: Credentials): ResolutionApi =
    new ResolutionApi(baseUrl, credentials)
}