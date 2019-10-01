package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.{Credentials, Resolution}
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class ResolutionApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  val resource = "resolutions"

  def all: F[Response[Seq[Resolution]]] =
    BacklogHttpDsl.get[Seq[Resolution]](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
