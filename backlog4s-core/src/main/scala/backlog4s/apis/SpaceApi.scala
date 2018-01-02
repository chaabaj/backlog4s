package backlog4s.apis

import backlog4s.datas.{Space, SpaceDiskUsage, SpaceNotification}
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object SpaceApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "space"

  def current: ApiPrg[Response[Space]] =
    get[Space](HttpQuery(s"$resource"))

  def logo: ApiPrg[Response[ByteStream]] =
    download(HttpQuery(s"$resource/image"))

  def notification: ApiPrg[Response[SpaceNotification]] =
    get[SpaceNotification](HttpQuery(s"$resource/notification"))

  def diskUsage: ApiPrg[Response[SpaceDiskUsage]] =
    get[SpaceDiskUsage](HttpQuery(s"$resource/diskUsage"))
}
