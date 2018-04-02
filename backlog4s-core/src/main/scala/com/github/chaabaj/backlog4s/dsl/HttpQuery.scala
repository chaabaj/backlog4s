package com.github.chaabaj.backlog4s.dsl

import com.github.chaabaj.backlog4s.datas.{Credentials, Id}

/**
  * Basic http query representation
 *
  * @param path a pathname /users for example
  * @param params a list of query parameters
  */
case class HttpQuery(
                      path: String,
                      params: Seq[QueryParam] = Seq(),
                      credentials: Credentials,
                      baseUrl: String
)

object HttpQuery {
  def apply(
             path: String,
             params: Seq[QueryParam] = Seq(),
             credentials: Credentials,
             baseUrl: String
           ): HttpQuery =
    new HttpQuery(path, params.filter(_.nonEmpty), credentials, baseUrl)
}
