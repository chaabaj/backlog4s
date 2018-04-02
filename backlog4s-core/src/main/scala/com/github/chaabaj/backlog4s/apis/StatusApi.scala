package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.{Credentials, Status}
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.Response
import com.github.chaabaj.backlog4s.dsl.HttpQuery
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class StatusApi(override val baseUrl: String,
                override val credentials: Credentials) extends Api {

  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "statuses"

  def all: ApiPrg[Response[Seq[Status]]] =
    get[Seq[Status]](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object StatusApi extends ApiContext[StatusApi] {
  override def apply(baseUrl: String, credentials: Credentials): StatusApi =
    new StatusApi(baseUrl, credentials)
}