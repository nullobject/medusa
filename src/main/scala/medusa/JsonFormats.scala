package medusa

import java.util.UUID
import spray.json._

object JsonFormats extends DefaultJsonProtocol {
  implicit object UUIDFormat extends RootJsonFormat[UUID] {
    def write(id: UUID) = JsString(id.toString)
    def read(value: JsValue) = value match {
      case JsString(s) => UUID.fromString(s)
      case _ => throw new DeserializationException("UUID expected")
    }
  }

  implicit object Vector2Format extends RootJsonFormat[Vector2] {
    def write(v: Vector2) = JsArray(JsNumber(v.x), JsNumber(v.y))
    def read(value: JsValue) = value match {
      case JsArray(JsNumber(x) :: JsNumber(y) :: Nil) => Vector2(x.toInt, y.toInt)
      case _ => throw new DeserializationException("Vector2 expected")
    }
  }

  implicit object ActionFormat extends RootJsonFormat[Action] {
    def write(action: Action) = action match {
      case Action.Idle => JsObject(
        "action" -> JsString(action.toString.toLowerCase)
      )
      case Action.Move => JsObject(
        "action" -> JsString(action.toString.toLowerCase)
      )
      case Action.Turn(direction) => JsObject(
        "action" -> JsString(action.toString.toLowerCase),
        "direction" -> direction.toJson
      )
      case Action.Attack => JsObject(
        "action" -> JsString(action.toString.toLowerCase)
      )
      case _ => throw new DeserializationException("Unknown action")
    }

    def read(value: JsValue) = {
      val fields = value.asJsObject.fields
      fields.get("action").get match {
        case JsString("idle")   => Action.Idle
        case JsString("move")   => Action.Move
        case JsString("turn")   => Action.Turn(fields.get("direction").get.convertTo[Int])
        case JsString("attack") => Action.Attack
        case _ => throw new DeserializationException("Action expected")
      }
    }
  }

  implicit object EntityStateNameFormat extends RootJsonFormat[Entity.StateName] {
    def write(state: Entity.StateName) = state match {
      case _ => JsString(state.toString.toLowerCase)
    }

    def read(value: JsValue) = {
      value match {
        case JsString("dead")      => Entity.Dead
        case JsString("idle")      => Entity.Idle
        case JsString("moving")    => Entity.Moving
        case JsString("turning")   => Entity.Turning
        case JsString("attacking") => Entity.Attacking
        case _ => throw new DeserializationException("State name expected")
      }
    }
  }

  implicit val requestFormat   = jsonFormat2(Game.Request.apply)
  implicit val entityFormat    = jsonFormat7(Entity.apply)
  implicit val worldViewFormat = jsonFormat2(WorldView.apply)
}
