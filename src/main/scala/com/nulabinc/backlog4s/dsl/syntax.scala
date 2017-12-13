package com.nulabinc.backlog4s.dsl

import com.nulabinc.backlog4s.dsl.ApiDsl.ApiPrg
import com.nulabinc.backlog4s.dsl.HttpADT.Response
import com.nulabinc.backlog4s.exceptions.BacklogApiException

object syntax {

  import ApiDsl.HttpOp._

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
