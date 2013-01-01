package medusa

import java.util.UUID
import scala.util.Random

/*
 * The world class represents the state of the world.
 */
case class World(
  age: Long = 0,
  entities: Set[Entity] = Set.empty
) {
  import World._

  def tick = tickEntities.incrementAge

  def spawn(entity: Entity) = copy(entities = entities + entity)

  def idle(entityId: UUID) = {
    val entity = getEntity(entityId).get
    val newEntity = entity.idle
    copy(entities = entities - entity + newEntity)
  }

  def move(entityId: UUID) = {
    val entity = getEntity(entityId).get
    val newEntity = entity.move
    copy(entities = entities - entity + newEntity)
  }

  def turn(entityId: UUID, direction: Int) = {
    val entity = getEntity(entityId).get
    val newEntity = entity.turn(direction)
    copy(entities = entities - entity + newEntity)
  }

  def attack(entityId: UUID) = {
    val entity = getEntity(entityId).get
    val newEntity = entity.attack
    copy(entities = entities - entity + newEntity)
  }

  private def getEntity(entityId: UUID): Option[Entity] = entities.find { _.id == entityId }
  private def incrementAge = copy(age = age + 1)
  private def tickEntities = copy(entities = entities.map(_.tick))
}

object World {
  case class InvalidOperationException(message: String) extends RuntimeException(message)
}
