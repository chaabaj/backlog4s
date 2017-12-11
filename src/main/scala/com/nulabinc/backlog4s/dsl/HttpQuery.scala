package com.nulabinc.backlog4s.dsl

import java.net.URL

import spray.json.JsValue

case class HttpQuery(
  url: String,
  params: Map[String, String] = Map()
)
