package backlog4s.apis

import backlog4s.datas.NoContent.NoContent
import backlog4s.datas.Order.Order
import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.dsl.{HttpQuery, QueryParam}
import org.joda.time.DateTime
import backlog4s.formatters.SprayJsonFormats._

class StarApi(override val baseUrl: String,
              override val credentials: Credentials) extends Api {
  import backlog4s.dsl.ApiDsl.HttpOp._

  def user(id: Id[User],
           minId: Option[Id[Star]] = None,
           maxId: Option[Id[Star]] = None,
           count: Long = 20,
           order: Order = Order.Desc): ApiPrg[Response[Seq[Star]]] = {
    val params = Seq(
      QueryParam.option("minId", minId),
      QueryParam.option("maxId", maxId),
      QueryParam("count", count),
      QueryParam("order", order.toString)
    )

    get[Seq[Star]](
      HttpQuery(
        s"users/${id.value}/stars",
        params,
        credentials,
        baseUrl
      )
    )
  }

  def count(id: Id[User],
            since: Option[DateTime] = None,
            until: Option[DateTime] = None): ApiPrg[Response[Count]] = {
    val params = Seq(
      QueryParam.option("since", since),
      QueryParam.option("until", until)
    )

    get[Count](
      HttpQuery(
        s"users/${id.value}/stars/count",
        params,
        credentials,
        baseUrl
      )
    )
  }

  def star(starForm: StarForm): ApiPrg[Response[NoContent]] =
    post[StarForm, NoContent](
      HttpQuery(path = "stars", credentials = credentials, baseUrl = baseUrl),
      starForm
    )
}

object StarApi extends ApiContext[StarApi] {
  override def apply(baseUrl: String, credentials: Credentials): StarApi =
    new StarApi(baseUrl, credentials)
}
