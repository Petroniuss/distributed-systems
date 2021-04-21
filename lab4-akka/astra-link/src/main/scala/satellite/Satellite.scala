package satellite

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import satellite.Satellite.{Command, Response}
import satellite.SatelliteAPI.getStatus
import satellite.Status

import scala.concurrent.Future
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
        // we could make an async call here, but we have a lot of threads
        // for actors as per configuration so that isn't necessary
        {
          val status = getStatus(satelliteIndex)
          val response = Response.StatusResponse(queryId, satelliteIndex, status)
          replyTo ! response
        }

        Behaviors.same
    }
  }

}

