
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
      projects <- ProjectApi.getAll().orFail
      categories <- CategoryApi.getAll(
        IdParam(projects.head.id)
      ).orFail
      milestones <- MilestoneApi.getAll(
        IdParam(projects.head.id)
      ).orFail
      issueTypes <- IssueTypeApi.getAll(
        IdParam(projects.head.id)
      ).orFail
      statuses <- StatusApi.getAll.orFail
      priorities <- PriorityApi.getAll.orFail
      resolutions <- ResolutionApi.getAll.orFail
      issues <- IssueApi.search(IssueSearch(count = 100)).orFail
      issue <- IssueApi.getById(IdParam(issues.head.id)).orFail
      countIssues <- IssueApi.count().orFail
    } yield countIssues

    prg.foldMap(interpreter).onComplete { result =>
      result match {
        case Success(data) => println(data)
        case Failure(ex) => ex.printStackTrace()
      }
      system.terminate()
    }
  }
}
