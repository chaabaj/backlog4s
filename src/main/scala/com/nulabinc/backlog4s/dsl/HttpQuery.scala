package com.nulabinc.backlog4s.dsl

/**
  * Basic http query representation
  * @param path a pathname /users for example
  * @param params a list of query parameters
  */
case class HttpQuery(
  path: String,
  params: Map[String, String] = Map()
)
