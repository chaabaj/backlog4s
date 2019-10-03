package com.github.chaabaj.backlog4s.apis

import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.dsl.{BacklogHttpDsl, HttpQuery}
import com.github.chaabaj.backlog4s.formatters.SprayJsonFormats._

class MilestoneApi[F[_]](baseUrl: String, credentials: Credentials)(implicit BacklogHttpDsl: BacklogHttpDsl[F]) {


  def resource(projectIdOrKey: IdOrKeyParam[Project]): String =
    s"projects/$projectIdOrKey/versions"

  def allOf(projectIdOrKey: IdOrKeyParam[Project]): F[Response[Seq[Milestone]]] =
    BacklogHttpDsl.get[Seq[Milestone]](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def add(projectIdOrKey: IdOrKeyParam[Project],
          form: AddMilestoneForm): F[Response[Milestone]] =
    BacklogHttpDsl.post[AddMilestoneForm, Milestone](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(projectIdOrKey: IdOrKeyParam[Project],
             id: Id[Milestone],
             form: UpdateMilestoneForm): F[Response[Milestone]] =
    BacklogHttpDsl.put[UpdateMilestoneForm, Milestone](
      HttpQuery(
        path = resource(projectIdOrKey),
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(projectIdOrKey: IdOrKeyParam[Project], id: Id[Milestone]): F[Response[Unit]] =
    BacklogHttpDsl.delete(
      HttpQuery(
        path = s"${resource(projectIdOrKey)}/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}
