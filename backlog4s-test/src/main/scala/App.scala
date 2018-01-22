
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis._
import backlog4s.datas._
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

    val httpInterpret = new AkkaHttpInterpret
    val interpreter = httpInterpret
    val accessKey = ApiKey.accessKey
    val api = AllApi.accessKey(baseUrl, accessKey)

    import api._

    val prg = for {
      projects <- projectApi.all().orFail
      categories <- categoryApi.allOf(
        IdParam(projects.head.id)
      ).orFail
      milestones <- milestoneApi.allOf(
        IdParam(projects.head.id)
      ).orFail
      issueTypes <- issueTypeApi.allOf(
        IdParam(projects.head.id)
      ).orFail
      statuses <- statusApi.all.orFail
      priorities <- priorityApi.all.orFail
      resolutions <- resolutionApi.all.orFail
      issues <- issueApi.search(IssueSearch(count = 100)).orFail
      issue <- issueApi.byIdOrKey(IdParam(issues.head.id)).orFail
      countIssues <- issueApi.count().orFail
      activities <- activityApi.space
      repositories <- gitApi.allOf(IdParam(projects.head.id)).orFail
      webhooks <- webhookApi.allOf(IdParam(projects.head.id)).orFail
    } yield webhooks

    val prg2 = userApi.create(
      AddUserForm(
        "userId",
        "vsdv",
        "vsd",
        ")@gmail.com",
        Role.Admin
      )
    )

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }

  def usingHammock(): Unit = {
    /*val hammockHttpInterpreter = new HammockInterpreter(
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
    }*/

  }

  def main(args: Array[String]): Unit = {
    usingAkka()
    //usingHammock()
  }
}
