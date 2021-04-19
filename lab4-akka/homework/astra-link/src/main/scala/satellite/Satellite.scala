package satellite

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import satellite.Satellite.{Command, Response}
import satellite.SatelliteAPI.getStatus
import satellite.Status

import scala.util.Random

object Satellite {
  enum Command {
    case StatusQuery(queryId: String, replyTo: ActorRef[Response])
  }

  enum Response {
    case StatusResponse(queryId: String, satelliteIndex: Int, status: Status)
  }

  def apply(satelliteIndex: Int): Behavior[Command] = {
    new Satellite(satelliteIndex).satellite()
  }
}

case class Satellite(satelliteIndex: Int) {

  def satellite(): Behavior[Command] = {
    Behaviors.receiveMessage {
      case Command.StatusQuery(queryId, replyTo) =>
        val status = getStatus(satelliteIndex)
        val response = Response.StatusResponse(queryId, satelliteIndex, status)

        // todo this one is blocking -> handle that carefully!
        replyTo ! response
        Behaviors.same
    }
  }

}

