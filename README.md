
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
3. [ ] GraphQL server (In progress)
4. [ ] Usage of auto code generation for protocol by analyse json files at compile-time or using cli tools

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
import backlog4s.apis._
import backlog4s.datas._
import backlog4s.interpreters.AkkaHttpInterpret
import scala.util.{Failure, Success}
import backlog4s.dsl.syntax._

object App {
  def main(args: Array[String]): Unit = {
      implicit val system = ActorSystem("test")
      implicit val mat = ActorMaterializer()
      implicit val exc = system.dispatcher
  
      if (args.length > 1) {
        val apiUrl = args.apply(0)
        val apiKey = args.apply(1)
        val httpInterpret = new AkkaHttpInterpret
        val interpreter = httpInterpret
        val allApi = AllApi.accessKey(baseUrl, ApiKey.accessKey)
          
        import allApi._
            
        // build a description of what we want to get
        val prg = for {
          projects <- projectApi.all().orFail
          issues <- issueApi.search(IssueSearch(projectIds = projects.map(_.id)))
        } yield issues
        
        // Evaluate the program 
        // This is here where the request will be sent to the backlog API
        interpreter.run(prg).onComplete {
          case Success(result) => println(result)
          case Failure(ex) => ex.printStackTrace()      
        }
      }
  }
}

```

## Stream API usage example

```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis._
import backlog4s.datas._
import backlog4s.interpreters.AkkaHttpInterpret
import scala.util.{Failure, Success}
import backlog4s.streaming.ApiStream
import backlog4s.dsl.syntax._

object App {
  def main(args: Array[String]): Unit = {
      implicit val system = ActorSystem("test")
      implicit val mat = ActorMaterializer()
      implicit val exc = system.dispatcher
  
      if (args.length > 1) {
        val apiUrl = args.apply(0)
        val apiKey = args.apply(1)
        val httpInterpret = new AkkaHttpInterpret
        val interpreter = httpInterpret
        val allApi = AllApi.accessKey(baseUrl, ApiKey.accessKey)
          
        import allApi._
        
        // Building the stream for issues
        val stream = ApiStream.sequential(10000)(
           (index, count) => issueApi.search(IssueSearch(offset = index, count = count))
        ).map { issues =>
           println(issues.map(_.summary).mkString("\n"))
           println()
           issues
        }
        
        // Evaluate the stream
        // This is here where the request will be sent to the backlog API
        interpreter.runStream(stream).onComplete {
          case Success(result) => println(result)
          case Failure(ex) => ex.printStackTrace()      
        }
      }
  }
}
```

## Parallelize requests

```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis._
import backlog4s.datas._
import backlog4s.interpreters.AkkaHttpInterpret
import scala.util.{Failure, Success}
import backlog4s.dsl.syntax._

object App {
  def main(args: Array[String]): Unit = {
      implicit val system = ActorSystem("test")
      implicit val mat = ActorMaterializer()
      implicit val exc = system.dispatcher
  
      if (args.length > 1) {
        val apiUrl = args.apply(0)
        val apiKey = args.apply(1)
        val httpInterpret = new AkkaHttpInterpret
        val interpreter = httpInterpret
        val allApi = AllApi.accessKey(baseUrl, ApiKey.accessKey)
          
        import allApi._
            
        // build a description of what we want to get
        val prg = for {
          projects <- projectApi.all().orFail
          issuesWithStatuses <- Seq(
            issueApi.search(IssueSearch(projectIds = projects.map(_.id))).orFail
            statusApi.all.orFail
          ).parallel
          
        } yield issuesWithStatuses
        
        // Evaluate the program 
        // This is here where the request will be sent to the backlog API
        interpreter.run(prg).onComplete {
          case Success(result) => println(result)
          case Failure(ex) => ex.printStackTrace()      
        }
      }
  }
}
```

