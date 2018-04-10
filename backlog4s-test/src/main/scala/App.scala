

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.chaabaj.backlog4s.apis._
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.interpreters.AkkaHttpInterpret
import com.github.chaabaj.backlog4s.streaming.ApiStream
import cats.effect.IO

import scala.util.{Failure, Success}
import hammock.jvm._

object App {

  implicit val hammockInterpreter = Interpreter[IO]
  import com.github.chaabaj.backlog4s.dsl.syntax._

  def usingAkka(apiUrl: String, apiKey: String): Unit = {
    implicit val system = ActorSystem("test")
    implicit val mat = ActorMaterializer()
    implicit val scheduler = monix.execution.Scheduler.Implicits.global

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

    val prg = for {
      issues <- issueApi.search().orFail
    } yield issues

    interpreter.run(prg).map { res =>
      println(res)
      interpreter.terminate()
    }.flatMap(_ => system.terminate())
      .onComplete {
        case Success(_) =>
          println("Terminated")
        case Failure(ex) =>
          ex.printStackTrace()
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