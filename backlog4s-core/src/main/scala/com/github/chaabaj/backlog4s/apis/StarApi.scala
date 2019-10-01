package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.NoContent.NoContent
import com.github.chaabaj.backlog4s.datas.Order.Order
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery, QueryParam}
import org.joda.time.DateTime
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class StarApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {

  def user(id: Id[User],
           minId: Option[Id[Star]] = None,
           maxId: Option[Id[Star]] = None,
           count: Long = 20,
           order: Order = Order.Desc): F[Response[Seq[Star]]] = {
    val params = Seq(
      QueryParam.option("minId", minId),
      QueryParam.option("maxId", maxId),
      QueryParam("count", count),
      QueryParam("order", order.toString)
    )

    BacklogHttpDsl.get[Seq[Star]](
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
            until: Option[DateTime] = None): F[Response[Count]] = {
    val params = Seq(
      QueryParam.option("since", since),
      QueryParam.option("until", until)
    )

    BacklogHttpDsl.get[Count](
      HttpQuery(
        s"users/${id.value}/stars/count",
        params,
        credentials,
        baseUrl
      )
    )
  }

  def star(starForm: StarForm): F[Response[NoContent]] =
    BacklogHttpDsl.post[StarForm, NoContent](
      HttpQuery(path = "stars", credentials = credentials, baseUrl = baseUrl),
      starForm
    )
}

