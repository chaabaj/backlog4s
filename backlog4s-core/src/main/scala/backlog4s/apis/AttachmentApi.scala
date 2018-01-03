package backlog4s.apis

import java.io.File
import backlog4s.datas.Attachment
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object AttachmentApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def send(file: File): ApiPrg[Response[Attachment]] =
    upload[Attachment](
      HttpQuery("space/attachment"),
      file
    )
}
