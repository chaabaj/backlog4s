package com.github.chaabaj.backlog4s.dsl

import cats.data.EitherT
import cats.{Monad, Traverse}
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response

object syntax {

  implicit class BacklogHttpSyntax[F[_], A](response: F[Response[A]])(implicit M: Monad[F]) {
    def handleError: EitherT[F, HttpError, A] = EitherT(response)
  }

}
