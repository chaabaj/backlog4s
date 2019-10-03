package com.github.chaabaj.backlog4s.streaming

import cats.Functor
import com.github.chaabaj.backlog4s.dsl.BacklogHttpDsl.Response
import com.github.chaabaj.backlog4s.exceptions.BacklogApiException
import monix.reactive.Observable
import org.reactivestreams.{Publisher, Subscriber}

object ApiStream {
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

  def publisher[F[_], A](limit: Int, start: Int = 0, step: Int = 100)(f: (Int, Int) => F[Response[Seq[A]]])(implicit Functor: Functor[F]): Publisher[Seq[A]] = {
    s: Subscriber[_ >: Seq[A]] => {
        def run(idx: Int): Unit = {
          if (idx >= limit)
            s.onComplete()
          else {
            var end = idx + step
            if (end > limit)
              end = limit
            Functor.map(f(idx, end)) {
              case Right(data) =>
                s.onNext(data)
                run(end)
              case Left(error) =>
                s.onError(BacklogApiException(error))
            }
          }
        }
        run(start)
      }
  }

  def stream[F[_], A](limit: Int, start: Int = 0, step: Int = 100)(f: (Int, Int) => F[Response[Seq[A]]])(implicit Functor: Functor[F]): Observable[Seq[A]] =
    Observable.fromReactivePublisher(publisher(limit, start, step)(f))
}
