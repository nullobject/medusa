package medusa

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSpec

class WorldTest extends FunSpec with MockFactory {
  describe("#tick") {
    it("should increment age") {
      val world = World(age = 0)
      assert(world.tick.age === 1)
    }

    it("should tick the entities") {
      val entity = stub[AbstractEntity]
      val world = World(entities = Set(entity))
      world.tick
      (entity.tick _).verify
    }
  }

  describe("#spawn") {
    it("should add the entity") {
      val entity = stub[AbstractEntity]
      val world = World(entities = Set.empty)
      assert(world.spawn(entity).entities.contains(entity))
    }
  }

  describe("#idle") {
    it("should idle the entity") {
      val id = UUID.randomUUID
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      val world = World(entities = Set(entity))
      world.idle(id)
      (entity.idle _).verify
    }
  }

  describe("#move") {
    it("should move the entity") {
      val id = UUID.randomUUID
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      val world = World(entities = Set(entity))
      world.move(id)
      (entity.move _).verify
    }
  }

  describe("#turn") {
    it("should turn the entity") {
      val id = UUID.randomUUID
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      val world = World(entities = Set(entity))
      world.turn(id, 90)
      (entity.turn _).verify(90)
    }
  }

  describe("#attack") {
    it("should attack the entity") {
      val id = UUID.randomUUID
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      val world = World(entities = Set(entity))
      world.attack(id)
      (entity.attack _).verify
    }
  }
}
