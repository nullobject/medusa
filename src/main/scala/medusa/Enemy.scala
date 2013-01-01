package medusa

import akka.actor.{Actor, LoggingFSM}
import akka.agent.Agent
import java.util.UUID
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * The enemy FSM controls the behaviour of an enemy actor.
 */
class Enemy(worldAgent: Agent[World]) extends Actor with LoggingFSM[Enemy.StateName, Enemy.StateData] {
  import Enemy._

  val spawnInterval = 1 second

  startWith(Dead, StateData(entityId = None))

  when(Dead) {
    case Event(Action.Idle, stateData: StateData) =>
      goto(Spawning)
  }

  when(Spawning) {
    case Event(Spawn, stateData: StateData) =>
      goto(Idle) using spawn(stateData)
  }

  when(Idle) {
    case Event(Game.Tick, stateData: StateData) =>
      stay using think(stateData)
  }

  whenUnhandled {
    case Event(Game.Tick, _) =>
      stay
  }

  onTransition {
    case _ -> Spawning =>
      setTimer("spawn", Spawn, spawnInterval, false)
  }

  initialize

  // Updates the enemy behaviour based on the world state.
  // TODO: move, turn, & attack!
  private def think(stateData: StateData) = {
    worldAgent.send(_.move(stateData.entityId.get))
    stateData
  }

  // Spawns an entity and replies with a world view.
  private def spawn(stateData: StateData) = {
    val entity = Entity()
    worldAgent.send(_.spawn(entity))
    stateData.copy(entityId = Some(entity.id))
  }
}

object Enemy {
  sealed trait StateName
  case object Dead     extends StateName
  case object Spawning extends StateName
  case object Idle     extends StateName

  case class StateData(entityId: Option[UUID])

  sealed trait Message
  case object Spawn extends Message
}
