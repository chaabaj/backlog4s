package com.github.backlog4s.dsl

import cats.effect.Sync
import cats.free.Free

import scala.util.control.NonFatal

/**
  * ApiDSL definition
  * here we can compose different DSL into one
  * that we will use for backlog4s
  */
object ApiDsl {
  type ApiADT[A] = HttpADT[A]
  type ApiPrg[A] = Free[ApiADT, A]

  val HttpOp = BacklogHttpOp
}