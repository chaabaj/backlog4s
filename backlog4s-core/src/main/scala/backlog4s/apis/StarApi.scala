package backlog4s.apis

import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.HttpQuery
import org.joda.time.DateTime
import backlog4s.formatters.SprayJsonFormats._
import backlog4s.utils.QueryParameter

object StarApi {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def user(id: Id[User],
           minId: Option[Id[Star]] = None,
           maxId: Option[Id[Star]] = None,
           count: Long = 20,
           order: Order = Order.Desc): ApiPrg[Response[Seq[Star]]] = {
    val params = Map(
      "minId" -> minId.map(_.value.toString).getOrElse(""),
      "maxId" -> maxId.map(_.value.toString).getOrElse(""),
      "count" -> count.toString,
      "order" -> order.toString
    )

    get[Seq[Star]](
      HttpQuery(
        s"users/${id.value}/stars",
        QueryParameter.removeEmptyValue(params)
      )
    )
  }

  def count(id: Id[User],
            since: Option[DateTime] = None,
            until: Option[DateTime] = None): ApiPrg[Response[Count]] = {
    val params = Map(
      "since" -> since
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse(""),
      "until" -> until
        .map(_.toString(DateTimeFormat.formatter))
        .getOrElse("")
    )

    get[Count](
      HttpQuery(
        s"users/${id.value}/stars/count",
        QueryParameter.removeEmptyValue(params)
      )
    )
  }

  // Api is not consistent here No Content is sent instead of a Star object
  def star(starForm: StarForm): ApiPrg[Response[Star]] =
    post[StarForm, Star](
      HttpQuery("stars"),
      starForm
    )
}
