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

  def all(projectIdOrKey: IdOrKeyParam[Project],
          path: String,
          order: Order = Order.Desc,
          offset: Long = 0,
          count: Long = 20): ApiPrg[Response[Seq[SharedFile]]] =
    get[Seq[SharedFile]](
      HttpQuery(s"projects/$projectIdOrKey/files/metadata/$path")
    )

  def linked(issueIdOrKey: IdOrKeyParam[Issue]): ApiPrg[Response[Seq[SharedFile]]] =
    get[Seq[SharedFile]](
      HttpQuery(s"issues/$issueIdOrKey/sharedFiles")
    )

  def downloadFile(projectIdOrKey: IdOrKeyParam[Project],
                   id: Id[SharedFile]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        s"projects/$projectIdOrKey/files/${id.value}"
      )
    )

  def link(issueIdOrKey: IdOrKeyParam[Issue],
           fileIds: NonEmptyList[Id[SharedFile]]): ApiPrg[Response[SharedFile]] =
    post[LinkFilesForm, SharedFile](
      HttpQuery(
        s"issues/$issueIdOrKey/sharedFiles"
      ),
      LinkFilesForm(fileIds.toList)
    )

  def unlink(issueIdOrKey: IdOrKeyParam[Issue],
             id: Id[SharedFile]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        s"issues/$issueIdOrKey/sharedFiles/${id.value}"
      )
    )
}
