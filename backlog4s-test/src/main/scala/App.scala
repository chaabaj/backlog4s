
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis.{GroupApi, ProjectApi, UserApi}
import backlog4s.datas.UserT
import backlog4s.interpreters.{AccessKey, AkkaHttpInterpret}

import scala.util.{Failure, Success}
import cats.implicits._

object App {

  import backlog4s.dsl.syntax._

  implicit val system = ActorSystem("test")
  implicit val mat = ActorMaterializer()
  implicit val exc = system.dispatcher

  def main(args: Array[String]): Unit = {
    val httpInterpret = new AkkaHttpInterpret(
      "https://nulab.backlog.jp/api/v2/", AccessKey(ApiKey.accessKey)
    )

    val interpreter = httpInterpret

    val prg = for {
      user <- UserApi.getById(UserT.myself).orFail
      projects <- ProjectApi.getAll().orFail
      project <- ProjectApi.getById(projects.head.id).orFail
      group <- GroupApi.getAll()
    } yield projects

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }
}
