package backlog4s.streaming

import backlog4s.dsl.ApiDsl.ApiPrg
import backlog4s.dsl.BacklogHttpOp
import backlog4s.dsl.HttpADT.Response
import cats.effect.Sync
import fs2.Stream

import scala.util.control.NonFatal

object Streaming {

  import backlog4s.dsl.ApiDsl.HttpOp._
  import backlog4s.dsl.syntax._

  type ApiResponse[A] = ApiPrg[Response[Seq[A]]]
  type ApiStream[A] = Stream[ApiPrg, Seq[A]]

  /**
    * Build a stream that fetch some data from start to limit by each step
    * This function covers only streaming over paginate api
    * This is the only use case we have for backlog4s
    * If we need to abstract more we will do it
    * @param limit
    * @param start
    * @param step
    * @param f a function that return the api call to get data from index to index + step
    * @tparam A type of the data to get
    * @return
    */
  def stream[A](limit: Int, start: Int = 0, step: Int = 100)(f: (Int) => ApiResponse[A]): ApiStream[A] = {
    Stream.unfoldEval[ApiPrg, Int, Seq[A]](start) { index =>
      if (index >= limit)
        pure[Option[(Seq[A], Int)]](None)
      else {
        f(index).orFail.map { result =>
          if (result.isEmpty) None
          else Some((result, index + step))
        }
      }
    }
  }
}

object StreamingEffect {
  implicit object ApiPrgSync extends Sync[ApiPrg] {
    override def suspend[A](thunk: => ApiPrg[A]): ApiPrg[A] =
      BacklogHttpOp.suspend(thunk)

    override def tailRecM[A, B](a: A)(f: A => ApiPrg[Either[A, B]]): ApiPrg[B] =
      f(a) flatMap {
        case Left(a) => tailRecM(a)(f)
        case Right(b) => pure(b)
      }

    override def flatMap[A, B](fa: ApiPrg[A])(f: A => ApiPrg[B]): ApiPrg[B] =
      fa.flatMap(f)
    
    // Delay throwing until its interpretation
    override def raiseError[A](e: Throwable): ApiPrg[A] =
      suspend(throw e)

    override def pure[A](x: A): ApiPrg[A] =
      BacklogHttpOp.pure(x)

    // If an exception is gonna to happen it's during the interpretation not here
    // They are maybe some way to implement here if i had ApiPrg[Throwable \/ A] but here
    // We don't know if fa during it's interpretation would fails
    // So don't call this method and expect this behavior it's not gonna to work
    // For IO monad it's perfectly normal but for Free monad this type signature isn't enough
    // To make it simple i don't know how to implement this without evaluate fa
    override def handleErrorWith[A](fa: ApiPrg[A])(f: Throwable => ApiPrg[A]): ApiPrg[A] = fa
  }
}
