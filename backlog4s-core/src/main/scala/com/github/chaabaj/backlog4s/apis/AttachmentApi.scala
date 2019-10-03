package com.github.chaabaj.backlog4s.apis

import java.io.File

import com.github.chaabaj.backlog4s.datas.{AccessKey, Attachment, Credentials, OAuth2Token}
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class AttachmentApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def send(file: File): F[Response[Attachment]] =
    BacklogHttpDsl.upload[Attachment](
      HttpQuery(
        path = "space/attachment",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      file
    )
}
