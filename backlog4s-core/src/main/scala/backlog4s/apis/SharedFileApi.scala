package backlog4s.apis

import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._
import cats.data.NonEmptyList

object SharedFileApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def allOf(projectIdOrKey: IdOrKeyParam[Project],
            path: String,
            order: Order = Order.Desc,
            offset: Long = 0,
            count: Long = 20): ApiPrg[Response[Seq[SharedFile]]] =
    get[Seq[SharedFile]](
      HttpQuery(s"projects/$projectIdOrKey/files/metadata/$path")
    )
}
