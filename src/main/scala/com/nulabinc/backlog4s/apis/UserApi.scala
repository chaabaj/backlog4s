package com.nulabinc.backlog4s.apis

import com.nulabinc.backlog4s.datas.{Id, User}
import com.nulabinc.backlog4s.dsl.ApiDsl.{ApiADT, ApiPrg}
import com.nulabinc.backlog4s.dsl.{HttpOp, HttpQuery, JsonProtocol}

object UserApi {
  import com.nulabinc.backlog4s.formatters.SprayJsonFormats._

  def byId(id: Id[User])(implicit I: HttpOp[ApiADT], D: JsonProtocol.ProtocolOp[ApiADT]): ApiPrg[Seq[User]] = {
    import I._
    import D._

    for {
      bytes <- get(HttpQuery(s"https://nulab.backlog.jp/api/v2/users"))
      user <- decode[Seq[User]](bytes)
    } yield user
  }
}
