package backlog4s.dsl

import cats.free.Free

/**
  * ApiDSL definition
  * here we can compose different DSL into one
  * that we will use for backlog4s
  */
object ApiDsl {
  type ApiADT[A] = HttpADT[A]
  type ApiPrg[A] = Free[ApiADT, A]

  val HttpOp = implicitly[BacklogHttpOp[ApiADT]]
}