package com.nulabinc.backlog4s.dsl

import cats.data.EitherK
import cats.free.Free

object ApiDsl {
  type ApiADT[A] = EitherK[HttpADT, JsonProtocol.ProtocolADT, A]
  type ApiPrg[A] = Free[ApiADT, A]
}

