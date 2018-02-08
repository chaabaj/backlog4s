package backlog4s.graphql

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{get, _}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import backlog4s.apis.AllApi
import backlog4s.interpreters.AkkaHttpInterpret
import sangria.execution.deferred.DeferredResolver
import sangria.parser.QueryParser
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import spray.json._

import scala.util.{Failure, Success}

object Api {
  val all = AllApi.accessKey(
    "https://nulab.backlog.jp/api/v2/",
    ApiKey.accessKey
  )
}

object GraphQLServer extends App {
  implicit val system = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()
  implicit val exc = system.dispatcher

  val interpreter = new AkkaHttpInterpret
  val schemaDefinition = new SchemaDefinition(interpreter)

  def parseProject(query: String, vars: JsObject, operation: Option[String]) = {
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        complete(Executor.execute(schemaDefinition.ProjectSchema, queryAst, new ProjectRepository,
          variables = vars,
          operationName = operation,
          deferredResolver = DeferredResolver.fetchers(schemaDefinition.projects))
          .map(OK → _)
          .recover {
            case error: QueryAnalysisError ⇒ BadRequest → error.resolveError
            case error: ErrorWithResolver ⇒ InternalServerError → error.resolveError
          })

      // can't parse GraphQL query, return error
      case Failure(error) ⇒
        complete(BadRequest, JsObject("error" → JsString(error.getMessage)))
    }
  }

  def parseStarWars(query: String, vars: JsObject, operation: Option[String]) = {
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        complete(Executor.execute(schemaDefinition.StarWarsSchema, queryAst, new CharacterRepo,
          variables = vars,
          operationName = operation,
          deferredResolver = DeferredResolver.fetchers(schemaDefinition.characters))
          .map(OK → _)
          .recover {
            case error: QueryAnalysisError ⇒ BadRequest → error.resolveError
            case error: ErrorWithResolver ⇒ InternalServerError → error.resolveError
          })

      // can't parse GraphQL query, return error
      case Failure(error) ⇒
        complete(BadRequest, JsObject("error" → JsString(error.getMessage)))
    }
  }

  val route: Route =
    post {
      entity(as[JsValue]) { requestJson ⇒
        val JsObject(fields) = requestJson

        val JsString(query) = fields("query")

        val operation = fields.get("operationName") collect {
          case JsString(op) ⇒ op
        }

        val vars = fields.get("variables") match {
          case Some(obj: JsObject) ⇒ obj
          case _ ⇒ JsObject.empty
        }

        parseProject(query, vars, operation)
      }
    } ~ get {
      complete(schemaDefinition.ProjectSchema.renderPretty)
    }



  Http().bindAndHandle(route, "0.0.0.0", 3000)
  println("Running")
}
