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

  // Example of users streaming
  /*def streamUser(userApi: UserApi, start: Int, limit: Int): ApiStream[User] = {
    var index = 0
    stream(() => {
      if (index < limit) {
        val res = Some(userApi.all(index))
        index += 100
        res
      } else None
    })
  }*/

  def stream[A](f: () => Option[T[A]]): ApiStream[A] = {
    Stream.unfoldEval[ApiPrg, Seq[A], Seq[A]](Seq.empty[A]) { acc =>
      f() match {
        case Some(prg) =>
          prg.orFail.map { result =>
            if (result.isEmpty) None
            else Some((acc, acc ++ result))
          }
        case None => pure[Option[(Seq[A], Seq[A])]](None)
      }
    }
  }
}
