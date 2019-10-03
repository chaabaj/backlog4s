package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.{Credentials, Priority, Resolution}
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class PriorityApi[F[_]](baseUrl: String,
                  credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  val resource = "priorities"

  def all: F[Response[Seq[Priority]]] =
    BacklogHttpDsl.get[Seq[Priority]](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
