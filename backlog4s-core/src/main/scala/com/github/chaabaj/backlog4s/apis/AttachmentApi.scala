package com.github.chaabaj.backlog4s.apis

import java.io.File

import com.github.chaabaj.backlog4s.datas.{AccessKey, Attachment, Credentials, OAuth2Token}
import com.github.chaabaj.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.chaabaj.backlog4s.dsl.HttpADT.Response
import com.github.chaabaj.backlog4s.dsl.HttpQuery
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class AttachmentApi(override val baseUrl: String,
                    override val credentials: Credentials) extends Api {
  import com.github.chaabaj.backlog4s.dsl.ApiDsl.HttpOp._

  def send(file: File): ApiPrg[Response[Attachment]] =
    upload[Attachment](
      HttpQuery(
        path = "space/attachment",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      file
    )
}

object AttachmentApi extends ApiContext[AttachmentApi] {
  override def apply(baseUrl: String, credentials: Credentials): AttachmentApi =
    new AttachmentApi(baseUrl, credentials)
}