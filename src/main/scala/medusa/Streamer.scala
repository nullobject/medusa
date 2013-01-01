package medusa

import akka.actor.Actor
import scala.concurrent.duration._
import scala.language.postfixOps
import spray.http._
import spray.routing.{ExceptionHandler, HttpServiceActor, RequestContext}
import spray.util.{IOClosed, SprayActorLogging}
import MediaTypes._

class Streamer(ctx: RequestContext) extends Actor with SprayActorLogging {
  import context._
  import CacheDirectives.`no-cache`
  import CustomMediaTypes._
  import HttpHeaders.{`Cache-Control`, Connection, RawHeader}

  def in[U](duration: FiniteDuration)(body: => U) {
    system.scheduler.scheduleOnce(duration, new Runnable { def run() { body } })
  }

  case object OK

  val responseStart = HttpResponse(
    entity = HttpBody(`text/event-stream`, ""),
    headers = List(`Cache-Control`(`no-cache`), `Connection`("Keep-Alive"), RawHeader("Access-Control-Allow-Origin", "*"))
  )

  ctx.responder ! ChunkedResponseStart(responseStart).withSentAck(OK)

  def sendDateTime = {
    val nextChunk = MessageChunk(s"data: ${DateTime.now.toIsoDateTimeString}\n\n")
    ctx.responder ! nextChunk.withSentAck(OK)
  }

  def receive = {
    case OK => in(1000 milliseconds) { sendDateTime }

    case x: IOClosed =>
      log.warning("Stopping response streaming due to {}", x.reason)
      stop(self)
  }
}

object CustomMediaTypes {
  import MediaTypes._
  val `text/event-stream` = register(CustomMediaType("text/event-stream"))
}
