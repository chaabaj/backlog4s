package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.{Credentials, Space, SpaceDiskUsage, SpaceNotification}
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.{ByteStream, Response}
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class SpaceApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  val resource = "space"

  def current: F[Response[Space]] =
    BacklogHttpDsl.get[Space](
      HttpQuery(
        path = s"$resource",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def logo: F[Response[ByteStream]] =
    BacklogHttpDsl.download(
      HttpQuery(
        path = s"$resource/image",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def notification: F[Response[SpaceNotification]] =
    BacklogHttpDsl.get[SpaceNotification](
      HttpQuery(
        path = s"$resource/notification",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def diskUsage: F[Response[SpaceDiskUsage]] =
    BacklogHttpDsl.get[SpaceDiskUsage](
      HttpQuery(
        path = s"$resource/diskUsage",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
