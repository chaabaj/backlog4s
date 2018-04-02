package com.github.backlog4s.apis

import com.github.backlog4s.datas.CustomForm.CustomForm
import com.github.backlog4s.datas._
import com.github.backlog4s.dsl.ApiDsl.ApiPrg
import com.github.backlog4s.dsl.HttpADT.Response
import com.github.backlog4s.dsl.HttpQuery
import com.github.backlog4s.formatters.SprayJsonFormats._

class CategoryApi(override val baseUrl: String,
                  override val credentials: Credentials) extends Api {
  import com.github.backlog4s.dsl.ApiDsl.HttpOp._

  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/categories"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): ApiPrg[Response[Seq[Category]]] =
    get[Seq[Category]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project], name: String): ApiPrg[Response[Category]] =
    post[CustomForm, Category](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "name" -> name
      )
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Category],
             newName: String): ApiPrg[Response[Category]] =
    put[CustomForm, Category](
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      Map(
        "name" -> newName
      )
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Category]): ApiPrg[Response[Unit]] =
    delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object CategoryApi extends ApiContext[CategoryApi] {
  override def apply(baseUrl: String, credentials: Credentials): CategoryApi =
    new CategoryApi(baseUrl, credentials)
}
