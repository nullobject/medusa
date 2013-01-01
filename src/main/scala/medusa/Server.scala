package medusa

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import java.util.UUID
import scala.concurrent.duration._
import scala.language.postfixOps
import spray.http._
import spray.httpx.SprayJsonSupport
import spray.routing.{ExceptionHandler, HttpServiceActor, RequestContext}
import spray.util.LoggingContext

/**
 * The server wraps a player action in a request and forwards it to the
 * game. When the game responds the server completes the request.
 */
class Server(game: ActorRef) extends Actor with HttpServiceActor with SprayJsonSupport {
  import CustomMediaTypes._
  import JsonFormats._
  import StatusCodes._

  implicit val timeout = Timeout(60 seconds)

  implicit def exceptionHandler(implicit log: LoggingContext) = ExceptionHandler.fromPF {
    case e: InvalidActionException => ctx =>
      log.warning("Request {} could not be handled normally", ctx.request)
      ctx.complete(UnprocessableEntity, e.getMessage)
  }

  def receive = runRoute {
    rewriteUnmatchedPath(rewritePath) {
      getFromResourceDirectory("www")
    } ~
    path("stream") {
      get {
        sendStreamingResponse
      }
    } ~
    headerValueByName("X-Player") { player =>
      path("actions") {
        post {
          entity(as[Action]) { action =>
            val playerId = UUID.fromString(player)
            val playerRequest = Game.Request(playerId, action)
            val response = ask(game, playerRequest).mapTo[WorldView]
            complete(response)
          }
        }
      }
    }
  }

  private def sendStreamingResponse(ctx: RequestContext) {
    actorRefFactory.actorOf(Props(new Streamer(ctx)))
  }

  private def rewritePath(path: String) = path match {
    case "/" => "index.html"
    case x if x.indexOf('.') > 0 => x
    case x => x + ".html"
  }
}
