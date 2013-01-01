package medusa

sealed trait Action

object Action {
  // Do nothing.
  case object Idle extends Action

  // Move the entity forward in its present direction.
  case object Move extends Action

  // Turn the entity to the given direction.
  case class Turn(direction: Int) extends Action

  // Make the entity attack in its present direction.
  case object Attack extends Action
}

abstract class InvalidActionException(message: String) extends RuntimeException(message)

case object InvalidActionException extends InvalidActionException("Invalid action")
