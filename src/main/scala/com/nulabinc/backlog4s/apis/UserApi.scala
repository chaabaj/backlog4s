package com.nulabinc.backlog4s.apis

import com.nulabinc.backlog4s.datas.{Id, User}
import com.nulabinc.backlog4s.dsl.ApiDsl.ApiPrg
import com.nulabinc.backlog4s.dsl.HttpQuery
import com.nulabinc.backlog4s.formatters.SprayJsonFormats._

object UserApi {

  import com.nulabinc.backlog4s.dsl.ApiDsl.HttpOp._
  import com.nulabinc.backlog4s.dsl.ApiDsl.ProtocolOp._

  private val resource = "users"

  def byId(id: Id[User]): ApiPrg[Seq[User]] = {
    for {
      bytes <- get(HttpQuery(resource))
      user <- decode[Seq[User]](bytes)
    } yield user
  }
}

