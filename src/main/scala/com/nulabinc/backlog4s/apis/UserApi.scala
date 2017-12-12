package com.nulabinc.backlog4s.apis

import com.nulabinc.backlog4s.datas.{Id, User}
import com.nulabinc.backlog4s.dsl.ApiDsl.ApiPrg
import com.nulabinc.backlog4s.dsl.HttpADT.Response
import com.nulabinc.backlog4s.dsl.HttpQuery
import com.nulabinc.backlog4s.formatters.SprayJsonFormats._

object UserApi {

  import com.nulabinc.backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "users"

  def getAll(offset: Int = 0, limit: Int = 100): ApiPrg[Response[Seq[User]]] = {
    get[Seq[User]](HttpQuery(resource))
  }

}

