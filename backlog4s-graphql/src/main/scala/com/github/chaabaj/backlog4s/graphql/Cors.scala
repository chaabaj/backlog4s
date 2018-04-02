package com.github.chaabaj.backlog4s.graphql

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.HttpOriginRange
import akka.http.scaladsl.server.Directive0
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.model.HttpHeaderRange
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings

import scala.collection.immutable.Seq

object Cors {

  def default(): Directive0 =
    cors(
      CorsSettings.Default(
        allowGenericHttpRequests = true,
        allowCredentials = true,
        allowedOrigins = HttpOriginRange.*,
        allowedHeaders = HttpHeaderRange.*,
        allowedMethods = Seq(GET, POST, HEAD, DELETE, OPTIONS),
        exposedHeaders = Seq.empty,
        maxAge = Some(30 * 60)
      )
    )
}
