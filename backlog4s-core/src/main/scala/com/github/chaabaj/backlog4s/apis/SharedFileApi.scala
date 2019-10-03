package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response

class SharedFileApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def allOf(projectIdOrKey: IdOrKeyParam[Project],
            path: String,
            order: Order = Order.Desc,
            offset: Long = 0,
            count: Long = 20): F[Response[Seq[SharedFile]]] =
    BacklogHttpDsl.get[Seq[SharedFile]](
      HttpQuery(
        path = s"projects/$projectIdOrKey/files/metadata/$path",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
