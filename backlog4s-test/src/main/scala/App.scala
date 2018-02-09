
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis._
import backlog4s.datas._
import backlog4s.interpreters.AkkaHttpInterpret
import backlog4s.streaming.ApiStream
import cats.effect.IO

import scala.util.{Failure, Success}
import hammock.jvm._

object App {

  implicit val hammockInterpreter = Interpreter[IO]
  val baseUrl = "https://nulab.backlog.jp/api/v2/"

  import backlog4s.dsl.syntax._

  def usingAkka(): Unit = {
    implicit val system = ActorSystem("test")
    implicit val mat = ActorMaterializer()
    implicit val exc = system.dispatcher

    val httpInterpret = new AkkaHttpInterpret
    val interpreter = httpInterpret
    val allApi = AllApi.accessKey(baseUrl, ApiKey.accessKey)

    import allApi._

    val stream = ApiStream.parallel(10000, 4)(
      (index, count) => issueApi.search(IssueSearch(offset = index, count = count))
    ).map { issues =>
      println(issues.map(_.summary).mkString("\n"))
      println()
      issues
    }


    for {
      issues <- issueApi.search().orFail
    } yield issues

    interpreter.runStream(stream).onComplete { result =>
      result match {
        case Success(data) => println("Stream processed")
        case Failure(ex) => ex.printStackTrace()
      }
    }
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
