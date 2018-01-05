package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

object UserApi {

  import backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "users"

  def all(offset: Int = 0, limit: Int = 100): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](HttpQuery(resource))

  def byId(id: Id[User]): ApiPrg[Response[User]] =
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

