
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis._
import backlog4s.datas.{AccessKey, IdParam, IssueSearch, UserT}
import backlog4s.interpreters.{AkkaHttpInterpret, HammockInterpreter}
import cats.effect.IO

import scala.util.{Failure, Success}
import cats.implicits._
import hammock.jvm._
import fs2._

object App {

  import backlog4s.dsl.syntax._

  implicit val hammockInterpreter = Interpreter[IO]
  val baseUrl = "https://nulab.backlog.jp/api/v2/"

  def usingAkka(): Unit = {
    implicit val system = ActorSystem("test")
    implicit val mat = ActorMaterializer()
    implicit val exc = system.dispatcher

    val httpInterpret = new AkkaHttpInterpret(
      baseUrl, AccessKey(ApiKey.accessKey)
    )
    val interpreter = httpInterpret

    val prg = for {
      projects <- ProjectApi.all().orFail
      categories <- CategoryApi.allOf(
        IdParam(projects.head.id)
      ).orFail
      milestones <- MilestoneApi.allOf(
        IdParam(projects.head.id)
      ).orFail
      issueTypes <- IssueTypeApi.allOf(
        IdParam(projects.head.id)
      ).orFail
      statuses <- StatusApi.all.orFail
      priorities <- PriorityApi.all.orFail
      resolutions <- ResolutionApi.all.orFail
      issues <- IssueApi.search(IssueSearch(count = 100)).orFail
      issue <- IssueApi.byIdOrKey(IdParam(issues.head.id)).orFail
      countIssues <- IssueApi.count().orFail
      activities <- ActivityApi.space
      repositories <- GitApi.allOf(IdParam(projects.head.id)).orFail
      webhooks <- WebhookApi.allOf(IdParam(projects.head.id)).orFail
    } yield webhooks

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }

  def usingHammock(): Unit = {
    val hammockHttpInterpreter = new HammockInterpreter(
      baseUrl, AccessKey(ApiKey.accessKey)
    )

    val prg = for {
      user <- UserApi.byId(UserT.myself).orFail
      icon <- UserApi.downloadIcon(user.id).orFail
      projects <- ProjectApi.all().orFail
    } yield icon

    val result = prg.foldMap(hammockHttpInterpreter).unsafeRunSync()
    result.map { buffer =>
      buffer
    }
  }

  def main(args: Array[String]): Unit = {
    //usingAkka()
    usingHammock()
  }
}
