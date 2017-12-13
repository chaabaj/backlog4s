package com.nulabinc.backlog4s

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.implicits._
import com.nulabinc.backlog4s.apis.UserApi
import com.nulabinc.backlog4s.datas.{User, UserT}
import com.nulabinc.backlog4s.dsl.HttpADT.ByteStream
import com.nulabinc.backlog4s.interpreters.{AccessKey, AkkaHttpInterpret}

import scala.util.{Failure, Success}
import dsl.syntax._

object App {

  implicit val system = ActorSystem("test")
  implicit val mat = ActorMaterializer()
  implicit val exc = system.dispatcher

  def main(args: Array[String]): Unit = {
    val httpInterpret = new AkkaHttpInterpret(
      "https://nulab.backlog.jp/api/v2/", AccessKey(Key.accessKey)
    )

    val interpreter = httpInterpret

    val prg = for {
      user <- UserApi.getById(UserT.myself).orFail[User]
      file <- UserApi.downloadIcon(user.id).orFail[ByteStream]
    } yield file

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => {
          println(data.run.unsafeRunSync())
        }
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }
}
