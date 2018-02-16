
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

  import backlog4s.dsl.syntax._

  def usingAkka(apiUrl: String, apiKey: String): Unit = {
    implicit val system = ActorSystem("test")
    implicit val mat = ActorMaterializer()
    implicit val exc = system.dispatcher

    val httpInterpret = new AkkaHttpInterpret
    val interpreter = httpInterpret
    val allApi = AllApi.accessKey(apiUrl, apiKey)

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
