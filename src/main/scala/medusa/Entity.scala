package medusa

import java.util.UUID

trait AbstractEntity {
  def canMove: Boolean
  def canTurn: Boolean
  def canAttack: Boolean
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

  def canMove   = energy >= 20
  def canTurn   = energy >= 30
  def canAttack = energy >= 100

  def tick = incrementAge

  def idle = changeEnergy(20).copy(state = Idle)

  def move = changeEnergy(-20).copy(position = position + (0, 1), state = Moving)

  def turn(d: Int) = changeEnergy(-30).copy(direction = d, state = Turning)

  def attack = changeEnergy(-100).copy(state = Attacking)

  private def incrementAge = copy(age = age + 1)
  private def changeEnergy(delta: Int) = copy(energy = math.min(math.max(0, energy + delta), 100))
}

object Entity {
  sealed trait StateName
  case object Dead       extends StateName
  case object Idle       extends StateName
  case object Moving     extends StateName
  case object Turning    extends StateName
  case object Attacking  extends StateName
}
