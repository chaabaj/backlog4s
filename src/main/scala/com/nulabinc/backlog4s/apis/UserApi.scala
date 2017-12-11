package com.nulabinc.backlog4s.apis

import com.nulabinc.backlog4s.datas.{Id, User}
import com.nulabinc.backlog4s.dsl.ApiDsl.ApiPrg
import com.nulabinc.backlog4s.dsl.HttpQuery
import com.nulabinc.backlog4s.formatters.SprayJsonFormats._

object UserApi {

  import com.nulabinc.backlog4s.dsl.ApiDsl.HttpOp._
  import com.nulabinc.backlog4s.dsl.ApiDsl.ProtocolOp._
  
  def byId(id: Id[User]): ApiPrg[Seq[User]] = {
    // Temporary just in this form for testing now
    for {
      bytes <- get(HttpQuery(s"https://nulab.backlog.jp/api/v2/users"))
      user <- decode[Seq[User]](bytes)
    } yield user
  }
}

