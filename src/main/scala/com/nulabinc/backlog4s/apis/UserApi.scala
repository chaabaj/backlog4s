package com.nulabinc.backlog4s.apis

import com.nulabinc.backlog4s.datas._
import com.nulabinc.backlog4s.dsl.ApiDsl.ApiPrg
import com.nulabinc.backlog4s.dsl.HttpADT.{ByteStream, Response}
import com.nulabinc.backlog4s.dsl.HttpQuery
import com.nulabinc.backlog4s.formatters.SprayJsonFormats._

object UserApi {

  import com.nulabinc.backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "users"

  def getAll(offset: Int = 0, limit: Int = 100): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](HttpQuery(resource))

  def getById(id: Id[User]): ApiPrg[Response[User]] =
    if (id == UserT.myself)
      get[User](HttpQuery(s"$resource/myself"))
    else
      get[User](HttpQuery(s"$resource/${id.value}"))

  def create(form: AddUserForm): ApiPrg[Response[User]] =
    post[AddUserForm, User](HttpQuery(resource), form)

  def update(id: Id[User], form: UpdateUserForm): ApiPrg[Response[User]] =
    put[UpdateUserForm, User](
      HttpQuery(s"$resource/${id.value}"),
      form
    )

  def remove(id: Id[User]): ApiPrg[Response[Unit]] =
    delete(HttpQuery(s"$resource/${id.value}"))

  def downloadIcon(id: Id[User]): ApiPrg[Response[ByteStream]] =
    download(HttpQuery(s"$resource/${id.value}/icon"))
}

