package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import cats.data.NonEmptyList
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class WikiApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  val resource = "wikis"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Seq[WikiSummary]]] =
    BacklogHttpDsl.get[Seq[WikiSummary]](
      HttpQuery(
        resource,
        QueryParam.single("projectIdOrKey", projectIdOrKey),
        credentials,
        baseUrl
      )
    )

  def byId(id: Id[Wiki]): F[Response[Wiki]] =
    BacklogHttpDsl.get[Wiki](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def count(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Count]] =
    BacklogHttpDsl.get[Count](
      HttpQuery(
        s"$resource/count",
        QueryParam.single("projectIdOrKey", projectIdOrKey),
        credentials,
        baseUrl
      )
    )

  def tags(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Seq[WikiTag]]] =
    BacklogHttpDsl.get[Seq[WikiTag]](
      HttpQuery(
        s"$resource/tags",
        QueryParam.single("projectIdOrKey", projectIdOrKey),
        credentials,
        baseUrl
      )
    )

  def attachments(id: Id[Wiki]): F[Response[Seq[Attachment]]] =
    BacklogHttpDsl.get[Seq[Attachment]](
      HttpQuery(
        path = s"$resource/${id.value}/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadAttachment(id: Id[Wiki], attachmentId: Id[Attachment]): F[Response[ByteStream]] =
    BacklogHttpDsl.download(
      HttpQuery(
        path = s"$resource/${id.value}/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def attach(id: Id[Wiki], attachmentIds: NonEmptyList[Id[Attachment]]): F[Response[Attachment]] =
    BacklogHttpDsl.post[AttachForm, Attachment](
      HttpQuery(
        path = s"$resource/${id.value}/attachments",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      AttachForm(attachmentIds.toList)
    )

  def removeAttachment(id: Id[Wiki], attachmentId: Id[Attachment]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"$resource/${id.value}/attachments/${attachmentId.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def sharedFiles(id: Id[Wiki]): F[Response[Seq[SharedFile]]] =
    BacklogHttpDsl.get[Seq[SharedFile]](
      HttpQuery(
        path = s"$resource/${id.value}/sharedFiles",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def history(id: Id[Wiki]): F[Response[Seq[WikiHistory]]] =
    BacklogHttpDsl.get[Seq[WikiHistory]](
      HttpQuery(
        path = s"$resource/${id.value}/history",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def stars(id: Id[Wiki]): F[Response[Seq[Star]]] =
    BacklogHttpDsl.get[Seq[Star]](
      HttpQuery(
        path = s"$resource/${id.value}/stars",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  // Don't understand the API yet def shareFile

  def add(form: AddWikiForm): F[Response[Wiki]] =
    BacklogHttpDsl.post[AddWikiForm, Wiki](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(id: Id[Wiki], form: UpdateWikiForm): F[Response[Wiki]] =
    BacklogHttpDsl.put[UpdateWikiForm, Wiki](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(id: Id[Wiki], mailNotify: Boolean = false): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        s"$resource/${id.value}",
        QueryParam.single("mailNotifiy", mailNotify),
        credentials,
        baseUrl
      )
    )
}
