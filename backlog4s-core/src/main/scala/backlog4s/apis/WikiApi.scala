package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import cats.data.NonEmptyList
import backlog4s.formatters.SprayJsonFormats._

object WikiApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "wikis"

  def all(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[WikiSummary]]] =
    get[Seq[WikiSummary]](
      HttpQuery(
        resource,
        Map(
          "projectIdOrKey" -> projectIdOrKey.toString
        )
      )
    )

  def count(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(
        s"$resource/count",
        Map(
          "projectIdOrKey" -> projectIdOrKey.toString
        )
      )
    )

  def tags(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[WikiTag]]] =
    get[Seq[WikiTag]](
      HttpQuery(
        s"$resource/tags",
        Map(
          "projectIdOrKey" -> projectIdOrKey.toString
        )
      )
    )

  def attachments(id: Id[Wiki]): ApiPrg[Response[Seq[Attachment]]] =
    get[Seq[Attachment]](
      HttpQuery(
        s"$resource/${id.value}/attachments"
      )
    )

  def downloadAttachment(id: Id[Wiki], attachmentId: Id[Attachment]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        s"$resource/${id.value}/attachments/${attachmentId.value}"
      )
    )

  def attach(id: Id[Wiki], attachmentIds: NonEmptyList[Id[Attachment]]): ApiPrg[Response[Attachment]] =
    post[AttachForm, Attachment](
      HttpQuery(
        s"$resource/${id.value}/attachments"
      ),
      AttachForm(attachmentIds.toList)
    )

  def removeAttachment(id: Id[Wiki], attachmentId: Id[Attachment]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        s"$resource/${id.value}/attachments/${attachmentId.value}"
      )
    )

  def sharedFiles(id: Id[Wiki]): ApiPrg[Response[Seq[SharedFile]]] =
    get[Seq[SharedFile]](
      HttpQuery(
        s"$resource/${id.value}/sharedFiles"
      )
    )

  def history(id: Id[Wiki]): ApiPrg[Response[Seq[WikiHistory]]] =
    get[Seq[WikiHistory]](
      HttpQuery(
        s"$resource/${id.value}/history"
      )
    )

  def stars(id: Id[Wiki]): ApiPrg[Response[Seq[Star]]] =
    get[Seq[Star]](
      HttpQuery(
        s"$resource/${id.value}/stars"
      )
    )

  // Don't understand the API yet def shareFile

  def add(form: AddWikiForm): ApiPrg[Response[Wiki]] =
    post[AddWikiForm, Wiki](
      HttpQuery(
        resource
      ),
      form
    )

  def update(id: Id[Wiki], form: UpdateWikiForm): ApiPrg[Response[Wiki]] =
    put[UpdateWikiForm, Wiki](
      HttpQuery(
        resource
      ),
      form
    )

  def remove(id: Id[Wiki], mailNotify: Boolean = false): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        s"$resource/${id.value}",
        Map(
          "mailNotify" -> mailNotify.toString
        )
      )
    )
}
