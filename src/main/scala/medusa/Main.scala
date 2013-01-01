package medusa

import akka.actor.Props
import akka.agent.Agent
import spray.can.server.SprayCanHttpServerApp

object Main extends App with SprayCanHttpServerApp {
  val world = World()
  val worldAgent = Agent(world)(system)
  var game = system.actorOf(Props(new Game(worldAgent)), name = "game")
  var server = system.actorOf(Props(new Server(game)), name = "server")

  game ! Game.Start
  newHttpServer(server) ! Bind(interface = "localhost", port = 8080)
}
