package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas.CustomForm.CustomForm
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class CategoryApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {
  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/categories"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Seq[Category]]] =
    BacklogHttpDsl.get[Seq[Category]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project], name: String): F[Response[Category]] =
    BacklogHttpDsl.post[CustomForm, Category](
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
             newName: String): F[Response[Category]] =
    BacklogHttpDsl.put[CustomForm, Category](
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
             id: Id[Category]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
