package medusa

import akka.actor.{Actor, FSM, Props}
import akka.agent.Agent
import java.util.UUID
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * The game FSM receives requests from the server and forwards them to a player.
 * On every tick the game sends its children a tick event.
 */
class Game(worldAgent: Agent[World]) extends Actor with FSM[Game.StateName, Game.StateData] {
  import Game._

  val tickInterval = 1 second

  startWith(Stopped, Uninitialized)

  when(Stopped) {
    case Event(Start, _) =>
      val enemyId = UUID.randomUUID
      val enemy = newEnemy(enemyId)
      enemy ! Action.Idle
      goto(Idle)
  }

  when(Idle) {
    case Event(Stop, _) =>
      goto(Stopped)

    case Event(Tick, _) =>
      worldAgent.send(_.tick)
      context.children.foreach { _ ! Tick }
      stay

    case Event(Request(playerId, action), _) =>
      getPlayer(playerId).forward(action)
      stay
  }

  onTransition {
    case _ -> Idle =>
      setTimer("tick", Tick, tickInterval, true)

    case Idle -> _ =>
      cancelTimer("tick")
  }

  initialize

  private def newEnemy(enemyId: UUID) =
    context.actorOf(Props(new Enemy(worldAgent)), name = s"enemy-$enemyId")

  private def getPlayer(playerId: UUID) =
    context.child(s"player-$playerId").getOrElse(newPlayer(playerId))

  private def newPlayer(playerId: UUID) =
    context.actorOf(Props(new Player(worldAgent)), name = s"player-$playerId")
}

object Game {
  case class Request(playerId: UUID, action: Action)

  sealed trait StateName
  case object Stopped extends StateName
  case object Idle    extends StateName

  sealed trait StateData
  case object Uninitialized extends StateData

  sealed trait Message
  case object Start extends Message
  case object Stop  extends Message
  case object Tick  extends Message
}
