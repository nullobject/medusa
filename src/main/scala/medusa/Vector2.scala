package medusa

import scala.language.implicitConversions

class Vector2(_1: Int, _2: Int) extends Tuple2[Int, Int](_1, _2) {
  def x = _1
  def y = _2
  def +(that: Vector2) = Vector2(this.x + that.x, this.y + that.y)
}

object Vector2 {
  def apply(x: Int, y: Int) = new Vector2(x, y)

  // Automatic conversion from a Tuple2 to a Vector2.
  implicit def tuple2ToVector2(t: (Int, Int)) = new Vector2(t._1, t._2)
}
