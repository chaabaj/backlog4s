package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.{ByteStream, Response}
import com.github.chaabaj.backlog4s.dsl.HttpQuery
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._
import cats.data.NonEmptyList

class SharedFileApi(override val baseUrl: String,
                    override val credentials: Credentials) extends Api{
  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  def allOf(projectIdOrKey: IdOrKeyParam[Project],
            path: String,
            order: Order = Order.Desc,
            offset: Long = 0,
            count: Long = 20): ApiPrg[Response[Seq[SharedFile]]] =
    get[Seq[SharedFile]](
      HttpQuery(
        path = s"projects/$projectIdOrKey/files/metadata/$path",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object SharedFileApi extends ApiContext[SharedFileApi] {
  override def apply(baseUrl: String, credentials: Credentials): SharedFileApi =
    new SharedFileApi(baseUrl, credentials)
}