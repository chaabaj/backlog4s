package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import cats.data.NonEmptyList
import backlog4s.formatters.SprayJsonFormats._

class WikiApi(override val baseUrl: String,
              override val credentials: Credentials) extends Api {
  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "wikis"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[WikiSummary]]] =
    get[Seq[WikiSummary]](
      HttpQuery(
        resource,
        Map(
          "projectIdOrKey" -> projectIdOrKey.toString
        ),
        credentials,
        baseUrl
      )
    )

  def count(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Count]] =
    get[Count](
      HttpQuery(
        s"$resource/count",
        Map(
          "projectIdOrKey" -> projectIdOrKey.toString
        ),
        credentials,
        baseUrl
      )
    )

  def tags(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[WikiTag]]] =
    get[Seq[WikiTag]](
      HttpQuery(
        s"$resource/tags",
        Map(
          "projectIdOrKey" -> projectIdOrKey.toString
        ),
        credentials,
        baseUrl
      )
    )

  def attachments(id: Id[Wiki]): ApiPrg[Response[Seq[Attachment]]] =
    get[Seq[Attachment]](
      HttpQuery(
        path = s"$resource/${id.value}/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadAttachment(id: Id[Wiki], attachmentId: Id[Attachment]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        path = s"$resource/${id.value}/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def attach(id: Id[Wiki], attachmentIds: NonEmptyList[Id[Attachment]]): ApiPrg[Response[Attachment]] =
    post[AttachForm, Attachment](
      HttpQuery(
        path = s"$resource/${id.value}/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      AttachForm(attachmentIds.toList)
    )

  def removeAttachment(id: Id[Wiki], attachmentId: Id[Attachment]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"$resource/${id.value}/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def sharedFiles(id: Id[Wiki]): ApiPrg[Response[Seq[SharedFile]]] =
    get[Seq[SharedFile]](
      HttpQuery(
        path = s"$resource/${id.value}/sharedFiles",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def history(id: Id[Wiki]): ApiPrg[Response[Seq[WikiHistory]]] =
    get[Seq[WikiHistory]](
      HttpQuery(
        path = s"$resource/${id.value}/history",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def stars(id: Id[Wiki]): ApiPrg[Response[Seq[Star]]] =
    get[Seq[Star]](
      HttpQuery(
        path = s"$resource/${id.value}/stars",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  // Don't understand the API yet def shareFile

  def add(form: AddWikiForm): ApiPrg[Response[Wiki]] =
    post[AddWikiForm, Wiki](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(id: Id[Wiki], form: UpdateWikiForm): ApiPrg[Response[Wiki]] =
    put[UpdateWikiForm, Wiki](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(id: Id[Wiki], mailNotify: Boolean = false): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        s"$resource/${id.value}",
        Map(
          "mailNotify" -> mailNotify.toString
        ),
        credentials,
        baseUrl
      )
    )
}

object WikiApi extends ApiContext[WikiApi] {
  override def apply(baseUrl: String, credentials: Credentials): WikiApi =
    new WikiApi(baseUrl, credentials)
}