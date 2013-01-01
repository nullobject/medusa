package medusa

import java.util.UUID

/*
 * The world class represents the state of the world.
 */
case class World(
  entities: Set[AbstractEntity] = Set.empty,
  age: Long = 0
) {
  import World._

  def tick = tickEntities.incrementAge

  def spawn(entity: AbstractEntity) = copy(entities = entities + entity)

  def idle(entityId: UUID) = {
    val entity = getEntity(entityId).get
    val newEntity = entity.idle
    replaceEntity(entity, newEntity)
  }

  def move(entityId: UUID) = {
    val entity = getEntity(entityId).get
    val newEntity = if (entity.canMove) entity.move else entity.idle
    replaceEntity(entity, newEntity)
  }

  def turn(entityId: UUID, direction: Int) = {
    val entity = getEntity(entityId).get
    val newEntity = if (entity.canTurn) entity.turn(direction) else entity.idle
    replaceEntity(entity, newEntity)
  }

  def attack(entityId: UUID) = {
    val entity = getEntity(entityId).get
    val newEntity = if (entity.canAttack) entity.attack else entity.idle
    replaceEntity(entity, newEntity)
  }

  private def replaceEntity(a: AbstractEntity, b: AbstractEntity) = copy(entities = entities - a + b)
  private def getEntity(entityId: UUID): Option[AbstractEntity] = entities.find { _.id == entityId }
  private def incrementAge = copy(age = age + 1)
  private def tickEntities = copy(entities = entities.map(_.tick))
}

object World {
  case class InvalidOperationException(message: String) extends RuntimeException(message)
}
