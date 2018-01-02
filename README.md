
# Backlog SDK for Scala

## Goals

1. [ ] Support All Backlog Api (In progress)
2. [x] Modular can use any http library if user want to switch.
       For now by default we use akka-http.
       Later we will provide support for others libraries
3. [ ] Data aggregation
4. [ ] Streaming support
5. [ ] Setup webhook
6. [ ] OAuth2 support

## Optional Goals and interesting experiment

1. [ ] Scalajs support
2. [ ] Proxy server
3. [ ] GraphQL server
4. [ ] Usage of auto code generation for protocol by analyse json files at compile-time or using cli tools

## Installation

Not published yet on maven use this git repository

## How to use

Using Akka Http:

```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.implicits._
import com.nulabinc.backlog4s.apis.UserApi
import com.nulabinc.backlog4s.datas.UserT
import com.nulabinc.backlog4s.interpreters.{AccessKey, AkkaHttpInterpret}

import scala.util.{Failure, Success}

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
      file <- UserApi.downloadIcon(UserT.id(0))
      user <- UserApi.getById(UserT.myself)
      users <- UserApi.getAll(0, 1000)
    } yield Seq(file, user, users)

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => data.foreach(println)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }
}
```
