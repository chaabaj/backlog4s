package backlog4s.apis

import backlog4s.datas.CustomForm.CustomForm
import backlog4s.datas.NoContent.NoContent
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import backlog4s.utils.QueryParameter
import backlog4s.formatters.SprayJsonFormats._


object WatchingApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  val resource = "watchings"

  def search(userId: Id[User],
             searchParams: WatchingSearch = WatchingSearch()): ApiPrg[Response[Seq[Watching]]] = {
    val params = Map(
      "order" -> searchParams.order.toString,
      "sort" -> searchParams.sort.toString,
      "count" -> searchParams.count.toString,
      "offset" -> searchParams.offset.toString,
      "resourceAlreadyRead" -> searchParams.resourceAlreadyRead
        .map(_.toString)
        .getOrElse(""),
      "issueId" -> searchParams.issueId.map(_.mkString(",")).getOrElse("")
    )

    get[Seq[Watching]](
      HttpQuery(
        s"users/${userId.value}/$resource",
        QueryParameter.removeEmptyValue(params)
      )
    )
  }

  def count(userId: Id[User],
            resourceAlreadyRead: Option[Boolean] = None,
            alreadyRead: Option[Boolean] = None): ApiPrg[Response[Count]] = {
    val params = Map(
      "resourceAlreadyRead" -> resourceAlreadyRead
        .map(_.toString)
        .getOrElse(""),
      "alreadyRead" -> alreadyRead.map(_.toString).getOrElse("")
    )

    get[Count](
      HttpQuery(
        s"users/${userId.value}/$resource/count"
      )
    )
  }

  def byId(id: Id[Watching]): ApiPrg[Response[Watching]] =
    get[Watching](
      HttpQuery(
        s"$resource/${id.value}"
      )
    )

  def add(form: AddWatchingForm): ApiPrg[Response[Watching]] =
    post[AddWatchingForm, Watching](
      HttpQuery(
        resource
      ),
      form
    )

  def update(id: Id[Watching], note: String): ApiPrg[Response[Watching]] =
    put[CustomForm, Watching](
      HttpQuery(
        s"$resource/${id.value}"
      ),
      Map(
        "note" -> note
      )
    )

  def remove(id: Id[Watching]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        s"$resource/${id.value}"
      )
    )

  def markAsRead(id: Id[Watching]): ApiPrg[Response[NoContent]] =
    post[CustomForm, NoContent](
      HttpQuery(
        s"$resource/${id.value}/markAsRead"
      ),
      Map()
    )
}
