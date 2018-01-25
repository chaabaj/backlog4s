package backlog4s.dsl

import backlog4s.datas.Credentials

/**
  * Basic http query representation
 *
  * @param path a pathname /users for example
  * @param params a list of query parameters
  */
case class HttpQuery(
  path: String,
  params: Map[String, String] = Map(),
  credentials: Credentials,
  baseUrl: String
)
