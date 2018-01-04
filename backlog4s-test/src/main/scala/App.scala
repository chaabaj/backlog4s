
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import backlog4s.apis._
import backlog4s.datas.{IdParam, IssueSearch, UserT}
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
      projects <- ProjectApi.all().orFail
      categories <- CategoryApi.all(
        IdParam(projects.head.id)
      ).orFail
      milestones <- MilestoneApi.all(
        IdParam(projects.head.id)
      ).orFail
      issueTypes <- IssueTypeApi.all(
        IdParam(projects.head.id)
      ).orFail
      statuses <- StatusApi.all.orFail
      priorities <- PriorityApi.all.orFail
      resolutions <- ResolutionApi.all.orFail
      issues <- IssueApi.search(IssueSearch(count = 100)).orFail
      issue <- IssueApi.byIdOrKey(IdParam(issues.head.id)).orFail
      countIssues <- IssueApi.count().orFail
      activities <- ActivityApi.space
      repositories <- GitApi.all(IdParam(projects.head.id)).orFail
    } yield repositories

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }
}
