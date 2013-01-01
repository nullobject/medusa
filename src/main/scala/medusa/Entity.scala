package medusa

import java.util.UUID

trait AbstractEntity {
  def id: UUID
  def tick: AbstractEntity
  def idle: AbstractEntity
  def move: AbstractEntity
  def turn(d: Int): AbstractEntity
  def attack: AbstractEntity
}

/**
 * The entity class represents an entity in the world.
 */
case class Entity(
  id: UUID = UUID.randomUUID,
  state: Entity.StateName = Entity.Idle,
  position: Vector2 = (0, 0),
  direction: Int = 0,
  health: Int = 100,
  energy: Int = 100,
  age: Long = 0
) extends AbstractEntity {
  import Entity._

  def isAlive = health > 0

  def tick = incrementAge

  def idle = copy(state = Idle)

  def move = copy(position = position + (0, 1), state = Moving)

  def turn(d: Int) = copy(direction = d, state = Turning)

  def attack = copy(state = Attacking)

  private def incrementAge = copy(age = age + 1)
}

object Entity {
  sealed trait StateName
  case object Dead       extends StateName
  case object Idle       extends StateName
  case object Moving     extends StateName
  case object Turning    extends StateName
  case object Attacking  extends StateName
}
