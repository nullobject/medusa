package medusa

import java.util.UUID
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSpec

class WorldTest extends FunSpec with MockFactory {
  val id = UUID.randomUUID

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
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      val world = World(entities = Set(entity))
      world.idle(id)
      (entity.idle _).verify
    }
  }

  describe("#move") {
    it("should move the entity if it can be moved") {
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      (entity.canMove _).when().returns(true)
      val world = World(entities = Set(entity))
      world.move(id)
      (entity.move _).verify
    }

    it("should not move the entity if it can't be moved") {
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      (entity.canMove _).when().returns(false)
      val world = World(entities = Set(entity))
      world.move(id)
      (entity.idle _).verify
    }
  }

  describe("#turn") {
    it("should turn the entity") {
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      (entity.canTurn _).when().returns(true)
      val world = World(entities = Set(entity))
      world.turn(id, 90)
      (entity.turn _).verify(90)
    }

    it("should not turn the entity if it can't be moved") {
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      (entity.canTurn _).when().returns(false)
      val world = World(entities = Set(entity))
      world.turn(id, 90)
      (entity.idle _).verify
    }
  }

  describe("#attack") {
    it("should attack the entity") {
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      (entity.canAttack _).when().returns(true)
      val world = World(entities = Set(entity))
      world.attack(id)
      (entity.attack _).verify
    }

    it("should not attack the entity if it can't be moved") {
      val entity = stub[AbstractEntity]
      (entity.id _).when().returns(id)
      (entity.canAttack _).when().returns(false)
      val world = World(entities = Set(entity))
      world.attack(id)
      (entity.idle _).verify
    }
  }
}
