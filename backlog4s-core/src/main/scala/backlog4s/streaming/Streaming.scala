package backlog4s.streaming

import backlog4s.apis.UserApi
import backlog4s.datas.User
import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import fs2.Stream

object Streaming {

  import backlog4s.dsl.ApiDsl.HttpOp._
  import backlog4s.dsl.syntax._

  type T[A] = ApiPrg[Response[Seq[A]]]

  type UserStream = Stream[ApiPrg, Seq[User]]
  type ApiStream[A] = Stream[ApiPrg, Seq[A]]

  def streamUser(userApi: UserApi, start: Int, limit: Int): ApiStream[User] =
    stream[User](1000)(index => userApi.all(index))

  def stream[A](limit: Int, start: Int = 0, step: Int = 100)(f: (Int) => T[A]): ApiStream[A] = {
    Stream.unfoldEval[ApiPrg, Int, Seq[A]](start) { index =>
      if (index >= limit)
        pure[Option[(Seq[A], Int)]](None)
      else {
        f().orFail.map { result =>
              if (result.isEmpty) None
              else Some((result, index + step))
        }
      }
    }
  }
}
