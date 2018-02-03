package backlog4s.apis

import backlog4s.datas._
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.{ByteStream, Response}
import backlog4s.dsl.HttpQuery
import backlog4s.formatters.SprayJsonFormats._

class UserApi(override val baseUrl: String,
              override val credentials: Credentials) extends Api {

  import backlog4s.dsl.ApiDsl.HttpOp._

  private val resource = "users"


  // stream[A](() => ApiPrg[Response[Seq[A]]): Stream[IO, Seq[A]]
  def all(offset: Int = 0, limit: Int = 100): ApiPrg[Response[Seq[User]]] =
    get[Seq[User]](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def byId(id: Id[User]): ApiPrg[Response[User]] = {
    val query = HttpQuery(
      path = s"$resource/myself",
      credentials = credentials,
      baseUrl = baseUrl
    )
    if (id == UserT.myself)
      get[User](query)
    else
      get[User](query.copy(path = s"$resource/${id.value}"))
  }


  def create(form: AddUserForm): ApiPrg[Response[User]] =
    post[AddUserForm, User](
      HttpQuery(
        path = resource,
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def update(id: Id[User], form: UpdateUserForm): ApiPrg[Response[User]] =
    put[UpdateUserForm, User](
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      ),
      form
    )

  def remove(id: Id[User]): ApiPrg[Response[User]] =
    delete(
      HttpQuery(
        path = s"$resource/${id.value}",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )

  def downloadIcon(id: Id[User]): ApiPrg[Response[ByteStream]] =
    download(
      HttpQuery(
        path = s"$resource/${id.value}/icon",
        credentials = credentials,
        baseUrl = baseUrl
      )
    )
}

object UserApi extends ApiContext[UserApi] {
  override def apply(baseUrl: String, credentials: Credentials): UserApi =
    new UserApi(baseUrl, credentials)
}