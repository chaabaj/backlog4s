

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.streaming.ApiStream
import cats.effect.IO
import com.github.chaabaj.backlog4s.apis.AllApi
import com.github.chaabaj.backlog4s.interpreters.BacklogHttpDslOnAkka

import scala.util.{Failure, Success}
import hammock.jvm._

object App {

  implicit val hammockInterpreter = Interpreter[IO]

  def usingAkka(apiUrl: String, apiKey: String): Unit = {
    implicit val system = ActorSystem("test")
    implicit val mat = ActorMaterializer()
    implicit val scheduler = monix.execution.Scheduler.Implicits.global

    val akkaHttpDsl = new BacklogHttpDslOnAkka()
    val allApi = new AllApi(apiUrl, AccessKey(apiKey))(akkaHttpDsl)
    import allApi._

    val stream = ApiStream.stream(10000, 4)(
      (index, count) => issueApi.search(IssueSearch(offset = index, count = count))
    ).map { issues =>
      println(issues.map(_.summary).mkString("\n"))
      println()
      issues
    }

    val prg = for {
      issues <- issueApi.search()
    } yield issues

    prg.runToFuture.onComplete { response =>
      response match {
        case Success(data) =>
          println(data)
        case Failure(ex) =>
          ex.printStackTrace()
      }
      akkaHttpDsl.terminate()
      system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.length > 1) {
      val apiUrl = args.apply(0)
      val apiKey = args.apply(1)
      usingAkka(apiUrl, apiKey)
    } else {
      println("Missing argument api url and api key")
    }
  }
}
