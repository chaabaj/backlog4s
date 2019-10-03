
# Backlog SDK for Scala

## Goals

1. [x] Support All public Backlog Api
2. [x] Modular can use any http library if user want to switch.
       For now by default we use akka-http.
       Later we will provide support for others libraries
4. [x] Streaming support
5. [ ] Setup webhook
6. [x] OAuth2 support

## Optional Goals and interesting experiment

1. [ ] Scalajs support
2. [ ] ~~GraphQL server~~
3. [ ] ~~Usage of auto code generation for protocol by analyse json files at compile-time or using cli tools~~

## Installation

### Add sonatype resolver

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
```

### Core

```scala
libraryDependencies += "com.github.chaabaj" %% "backlog4s-core" % "0.7.1-SNAPSHOT"
```

### Using Akka to execute the requests

```scala
libraryDependencies += "com.github.chaabaj" %% "backlog4s-akka" % "0.7.1-SNAPSHOT"
```

## Simple API usage

Look at backlog4s-test contain a program sample

```scala

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.streaming.ApiStream
import com.github.chaabaj.backlog4s.apis.AllApi
import com.github.chaabaj.backlog4s.interpreters.BacklogHttpDslOnAkka

import scala.util.{Failure, Success}
import com.github.chaabaj.backlog4s.dsl.syntax._

object App {

  def usingAkka(apiUrl: String, apiKey: String): Unit = {
    implicit val system = ActorSystem("test")
    implicit val mat = ActorMaterializer()
    implicit val scheduler = monix.execution.Scheduler.Implicits.global

    val akkaHttpDsl = new BacklogHttpDslOnAkka()
    val allApi = new AllApi(apiUrl, AccessKey(apiKey))(akkaHttpDsl)
    import allApi._

    val task = for {
      projects <- projectApi.all().handleError
      issues <- issueApi.search(IssueSearch(projectIds = projects.map(_.id))).handleError
    } yield issues

    task.value.runToFuture.onComplete { response =>
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

```

## Stream API usage example

```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.chaabaj.backlog4s.datas._
import com.github.chaabaj.backlog4s.streaming.ApiStream
import com.github.chaabaj.backlog4s.apis.AllApi
import com.github.chaabaj.backlog4s.interpreters.BacklogHttpDslOnAkka

import scala.util.{Failure, Success}
import com.github.chaabaj.backlog4s.dsl.syntax._

object App {

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

    stream.foreach(println).onComplete { response =>
      response match {
        case Success(_) =>
          println("Done")
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
```
