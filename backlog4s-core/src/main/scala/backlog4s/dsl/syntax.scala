package backlog4s.dsl

import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.HttpADT.Response
import backlog4s.exceptions.BacklogApiException

object syntax {

  import ApiDsl.HttpOp._

  // Extension methods to easily stop execution of
  // program when the program cannot continue
  // This will likely to change when i understand more
  // how to use FreeT monad transformer
  implicit class ResponseOps[A](response: Response[A]) {
    def orFail[A]: ApiPrg[A] =
      response match {
        case Right(value) => pure(value.asInstanceOf[A])
        case Left(error) => throw BacklogApiException(error)
      }
  }

  implicit class ApiOps[A](apiPrg: ApiPrg[Response[A]]) {
    def orFail[A]: ApiPrg[A] =
      apiPrg.flatMap {
        case Right(value) => pure(value.asInstanceOf[A])
        case Left(error) => throw BacklogApiException(error)
      }
  }
}
