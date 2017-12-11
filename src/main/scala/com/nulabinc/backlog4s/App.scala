package com.nulabinc.backlog4s

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.implicits._
import com.nulabinc.backlog4s.apis.UserApi
import com.nulabinc.backlog4s.datas.UserT
import dsl.HttpOp._
import dsl.JsonProtocol.ProtocolOp._
import dsl._

import scala.util.{Failure, Success}

object App {

  implicit val system = ActorSystem("test")
  implicit val mat = ActorMaterializer()
  implicit val exc = system.dispatcher

  val accessKey = "Hello"

  def main(args: Array[String]): Unit = {
    val httpInterpret = new AkkaHttpInterpret(AccessKey(accessKey))

    val interpreter = httpInterpret or SprayJsonProtocolDsl

    UserApi.byId(UserT.id(2)).foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data.toString)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }
}
