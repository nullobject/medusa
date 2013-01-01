package medusa

import org.scalatest.FunSpec

class EntityTest extends FunSpec {
  describe("#isAlive") {
    it("should return true if health is non-zero") {
      val entity = Entity(health = 1)
      assert(entity.isAlive)
    }

    it("should return false if health is zero") {
      val entity = Entity(health = 0)
      assert(!entity.isAlive)
    }
  }

  describe("#tick") {
    it("should increment age") {
      val entity = Entity(age = 0)
      assert(entity.tick.age === 1)
    }
  }

  describe("#idle") {
    it("should set the state to Idle") {
      val entity = Entity(state = Entity.Dead)
      assert(entity.idle.state === Entity.Idle)
    }

    it("should change energy") {
      val entity = Entity(energy = 0)
      assert(entity.idle.energy === 20)
    }
  }

  describe("#move") {
    it("should set the state to Moving") {
      val entity = Entity(state = Entity.Dead)
      assert(entity.move.state === Entity.Moving)
    }

    it("should change the position") {
      pending
    }

    it("should change energy") {
      val entity = Entity(energy = 100)
      assert(entity.move.energy === 80)
    }
  }

  describe("#turn") {
    it("should set the state to Turning") {
      val entity = Entity(state = Entity.Dead)
      assert(entity.turn(90).state === Entity.Turning)
    }

    it("should set the direction") {
      val entity = Entity(direction = 0)
      assert(entity.turn(90).direction === 90)
    }

    it("should change energy") {
      val entity = Entity(energy = 100)
      assert(entity.turn(90).energy === 70)
    }
  }

  describe("#attack") {
    it("should set the state to Attacking") {
      val entity = Entity(state = Entity.Dead)
      assert(entity.attack.state === Entity.Attacking)
    }

    it("should change energy") {
      val entity = Entity(energy = 100)
      assert(entity.attack.energy === 0)
    }
  }
}
