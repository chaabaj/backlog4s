
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis._
import backlog4s.datas._
import backlog4s.interpreters.{AkkaHttpInterpret, HammockInterpreter}
import backlog4s.streaming.ApiStream
import cats.effect.IO

import scala.util.{Failure, Success}
import cats.implicits._
import hammock.jvm._

object App {

  import backlog4s.dsl.syntax._
  import backlog4s.streaming.StreamingEffect._

  implicit val hammockInterpreter = Interpreter[IO]
  val baseUrl = "https://nulab.backlog.jp/api/v2/"

  def usingAkka(): Unit = {
    implicit val system = ActorSystem("test")
    implicit val mat = ActorMaterializer()
    implicit val exc = system.dispatcher

    val httpInterpret = new AkkaHttpInterpret
    val interpreter = httpInterpret
    val allApi = AllApi.accessKey(baseUrl, ApiKey.accessKey)

    import allApi._

    val issueStream = ApiStream.parallel(1000, 4)(
      (index, count) => issueApi.search(IssueSearch(offset = index, count = count))
    )

    val issuesPrg = Seq.range(0, 2000, 100).map(index => issueApi.search(IssueSearch(offset = index)).orFail)

    issueStream.compile.drain.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println("Stream processed")
        case Failure(ex) => ex.printStackTrace()
      }
    }
    /*issuesPrg.sequence.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
    }*/
    /*issueStream.compile.toVector.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }*/

    /*prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }*/
  }

  def usingHammock(): Unit = {
    /*val hammockHttpInterpreter = new HammockInterpreter(
      baseUrl, AccessKey(ApiKey.accessKey)
    )

    val prg = for {
      user <- UserApi.byId(UserT.myself).orFail
      icon <- UserApi.downloadIcon(user.id).orFail
      projects <- ProjectApi.all().orFail
    } yield icon

    val result = prg.foldMap(hammockHttpInterpreter).unsafeRunSync()
    result.map { buffer =>
      buffer
    }*/

  }


  def main(args: Array[String]): Unit = {
    usingAkka()
    //usingHammock()
  }
}
