package backlog4s.apis

import backlog4s.datas.{Credentials, Space, SpaceDiskUsage, SpaceNotification}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

class SpaceApi(override val baseUrl: String,
               override val credentials: Credentials) extends Api {
  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "space"

  def current: ApiPrg[Response[Space]] =
    get[Space](
      HttpQuery(
        path = s"$resource",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def logo: ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        path = s"$resource/image",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def notification: ApiPrg[Response[SpaceNotification]] =
    get[SpaceNotification](
      HttpQuery(
        path = s"$resource/notification",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def diskUsage: ApiPrg[Response[SpaceDiskUsage]] =
    get[SpaceDiskUsage](
      HttpQuery(
        path = s"$resource/diskUsage",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object SpaceApi extends ApiContext[SpaceApi] {
  override def apply(baseUrl: String, credentials: Credentials): SpaceApi =
    new SpaceApi(baseUrl, credentials)
}
