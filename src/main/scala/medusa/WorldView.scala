package medusa

/**
 * A world view is a subset of the world state from a player's perspective.
 */
case class WorldView(
  age: Long,
  entities: Set[Entity]
)

object WorldView {
  def scope(world: World) =
    WorldView(age = world.age, entities = world.entities)
}
