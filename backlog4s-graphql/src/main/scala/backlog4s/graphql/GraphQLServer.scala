package backlog4s.graphql

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{get, _}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import backlog4s.apis.AllApi
import backlog4s.interpreters.{AkkaHttpInterpret, HammockInterpreter}
import cats.effect.IO
import com.typesafe.config.ConfigFactory
import hammock.jvm.Interpreter
import sangria.execution.deferred.DeferredResolver
import sangria.parser.QueryParser
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import spray.json._

import scala.util.{Failure, Success}

object GraphQLServer {
  implicit val hammockInterpreter = Interpreter[IO]
  implicit val system = ActorSystem("sangria-server", ConfigFactory.load())
  implicit val materializer = ActorMaterializer()
  implicit val exc = system.dispatcher
  val hammockHttpInterpreter = new HammockInterpreter()

  def main(args: Array[String]): Unit = {
    if (args.length > 1) {
      val apiUrl = args.apply(0)
      val apiKey = args.apply(1)
      val interpreter = hammockHttpInterpreter
      val allApi = AllApi.accessKey(apiUrl, apiKey)
      val schemaDefinition = new SchemaDefinition(interpreter, allApi)

      def parseProject(query: String, vars: JsObject, operation: Option[String]) = {
        QueryParser.parse(query) match {

          // query parsed successfully, time to execute it!
          case Success(queryAst) ⇒
            complete(Executor.execute(schemaDefinition.ProjectSchema, queryAst, new BacklogRepository(allApi),
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

      val route: Route =
        Cors.default() {
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
        }

      Http().bindAndHandle(route, "0.0.0.0", 3000)
      println("Running")
    }
  }
}
