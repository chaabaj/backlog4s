
import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis.UserApi
import backlog4s.datas.{User, UserT}
import backlog4s.dsl.HttpADT.ByteStream
import backlog4s.interpreters.{AccessKey, AkkaHttpInterpret}
import fs2.Chunk

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
      user <- UserApi.getById(UserT.myself).orFail[User]
      file <- UserApi.downloadIcon(user.id).orFail[ByteStream]
    } yield file

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) =>
          println(data.chunks.runFold(Seq.empty[Chunk[ByteBuffer]]) {
            case (acc, bytes) => acc :+ bytes
          }.unsafeRunSync())
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }
}
