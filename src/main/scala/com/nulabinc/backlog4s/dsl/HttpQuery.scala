package com.nulabinc.backlog4s.dsl

case class HttpQuery(
  url: String,
  params: Map[String, String] = Map()
)
