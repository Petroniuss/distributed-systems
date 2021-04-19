import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import Satellite.{Command, Response}

object Satellite {
  enum Command {
    case StatusQuery(queryId: String, replyTo: ActorRef[Response])
  }

  enum Response {
    case StatusResponse(queryId: String, status: Status)
  }

  def apply(satelliteIndex: Int): Behavior[Command] = {
    new Satellite(satelliteIndex).satellite()
  }
}

case class Satellite(satelliteIndex: Int) {

  def satellite(): Behavior[Command] = {
    Behaviors.receiveMessage {
      case Command.StatusQuery(queryId, replyTo) =>
        val status = SatelliteAPI.getStatus(satelliteIndex)
        val response = Response.StatusResponse(queryId, status)

        // todo this one is blocking -> handle that carefully!
        replyTo ! response
        Behaviors.same
    }
  }

}
