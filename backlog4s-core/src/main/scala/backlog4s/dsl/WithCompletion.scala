package backlog4s.dsl


import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait WithCompletion[F[_]] {
  def onComplete[A](l: F[A])(f: Try[A] => Unit): F[A]
}

trait WithFutureCompletion extends WithCompletion[Future] {
  implicit def exc: ExecutionContext

  override def onComplete[A](future: Future[A])(f: Try[A] => Unit): Future[A] = {
    future.onComplete(f)
    future
  }
}