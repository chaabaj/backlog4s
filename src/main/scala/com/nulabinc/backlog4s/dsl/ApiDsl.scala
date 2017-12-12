package com.nulabinc.backlog4s.dsl

import cats.free.Free

object ApiDsl {
  type ApiADT[A] = HttpADT[A]
  type ApiPrg[A] = Free[ApiADT, A]

  val HttpOp = implicitly[BacklogHttpOp[ApiADT]]
}

