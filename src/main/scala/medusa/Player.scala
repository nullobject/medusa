package medusa

import akka.actor.{Actor, ActorRef, LoggingFSM, Status}
import akka.agent.Agent
import akka.util.Timeout
import java.util.UUID
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * The player FSM receives actions and executes them on the world agent.
 */
class Player(worldAgent: Agent[World]) extends Actor with LoggingFSM[Player.StateName, Player.StateData] {
  import Player._

  implicit val timeout = Timeout(5 seconds)

  val spawnInterval = 1 second

  startWith(Dead, StateData(senders = List.empty, entityId = None, transform = None))

  when(Dead) {
    case Event(Action.Idle, stateData: StateData) =>
      goto(Spawning) using stateData.copy(senders = stateData.senders :+ sender)
  }

  when(Spawning) {
    case Event(Spawn, stateData: StateData) =>
      goto(Idle) using spawn(stateData)
  }

  when(Idle) {
    case Event(Action.Idle, stateData: StateData) =>
      goto(Queued) using queue(stateData) { (entityId) => _.idle(entityId) }

    case Event(Action.Move, stateData: StateData) =>
      goto(Queued) using queue(stateData) { (entityId) => _.move(entityId) }

    case Event(Action.Turn(direction), stateData: StateData) =>
      goto(Queued) using queue(stateData) { (entityId) => _.turn(entityId, direction) }

    case Event(Action.Attack, stateData: StateData) =>
      goto(Queued) using queue(stateData) { (entityId) => _.attack(entityId) }
  }

  when(Queued) {
    case Event(Game.Tick, stateData: StateData) =>
      goto(Idle) using execute(stateData)
  }

  whenUnhandled {
    case Event(Game.Tick, _) =>
      stay

    case Event(_: Action, _) =>
      sender ! Status.Failure(InvalidActionException)
      stay
  }

  onTransition {
    case _ -> Spawning =>
      setTimer("spawn", Spawn, spawnInterval, false)
  }

  initialize

  // Queues a transform to be executed at the next tick.
  private def queue(stateData: StateData)(transform: UUID => World => World) =
    stateData.copy(senders = stateData.senders :+ sender, transform = Some(transform))

  // Executes the queued transform and replies with a world view.
  private def execute(stateData: StateData) = {
    worldAgent.send(stateData.transform.get(stateData.entityId.get))
    val worldView = WorldView.scope(worldAgent.await)
    stateData.senders.map { _ ! worldView }
    stateData.copy(senders = List.empty)
  }

  // Spawns an entity and replies with a world view.
  private def spawn(stateData: StateData) = {
    val entity = Entity()
    worldAgent.send(_.spawn(entity))
    val worldView = WorldView.scope(worldAgent.await)
    stateData.senders.map { _ ! worldView }
    stateData.copy(senders = List.empty, entityId = Some(entity.id))
  }
}

object Player {
  sealed trait StateName
  case object Dead     extends StateName
  case object Spawning extends StateName
  case object Idle     extends StateName
  case object Queued   extends StateName

  case class StateData(senders: List[ActorRef], entityId: Option[UUID], transform: Option[UUID => World => World])

  sealed trait Message
  case object Spawn extends Message
}
