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
    def orFail: ApiPrg[A] =
      response match {
        case Right(value) => pure(value.asInstanceOf[A])
        case Left(error) => throw BacklogApiException(error)
      }
  }

  implicit class ApiOps[A](apiPrg: ApiPrg[Response[A]]) {
    def orFail: ApiPrg[A] =
      apiPrg.flatMap {
        case Right(value) => pure(value)
        case Left(error) => throw BacklogApiException(error)
      }
  }

  implicit class ApiSeqOps[A](apiPrg: Seq[ApiPrg[A]]) {
    // Allow to run a sequence of operation
    // This is not running in parallel still sequential
    // I need to study about combining Free monad and Free applicative
    // To understand how can i run multiple program step in parallel
    // Also i want to keep the syntax of writing Api program
    // as simple as possible the performance cost will be here
    // important because we will not be able to exploit multiple cores
    // to dispatch multiple api request at the same time
    // For now this is an enough solution
    def sequence: ApiPrg[Seq[A]] =
      apiPrg.foldLeft(pure(Seq.empty[A])) {
        case (newPrg, prg) =>
          newPrg.flatMap { results =>
            prg.map { result =>
              results :+ result
            }
          }
      }
  }
}
