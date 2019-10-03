package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class ActivityApi[F[_]](baseUrl: String,
                        credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def user(id: Id[User]): F[Response[Seq[Activity]]] =
    BacklogHttpDsl.get[Seq[Activity]](
      HttpQuery(
        path = s"users/${id.value}/activities",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def space: F[Response[Seq[Activity]]] =
    BacklogHttpDsl.get[Seq[Activity]](
      HttpQuery(
        path = "space/activities",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
