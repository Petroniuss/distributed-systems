import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import Dispatcher.Command
import Dispatcher.Command.SatelliteStatusQuery
import Dispatcher.Command.WrappedSatelliteResponse
import Station.{Response, SatelliteResponse}
import satellite.Satellite
import satellite.Satellite.Response.StatusResponse

object Dispatcher {
  enum Command {
    case SatelliteStatusQuery(queryId: String, satelliteIndex: Int, replyTo: ActorRef[SatelliteResponse])

    case WrappedSatelliteResponse(satelliteResponse: Satellite.Response)
  }

  def apply(): Behavior[Command] = {
    Behaviors.setup(context => {
      val refs = Range(100, 200).toList
        .map(idx => context.spawn(Satellite(idx), s"satellite.Satellite-$idx"))

      val responseMapper = context.messageAdapter( response =>
        WrappedSatelliteResponse(response)
      )

      def active(inProgress: Map[String, ActorRef[SatelliteResponse]]): Behavior[Command] = {
        Behaviors.receiveMessage {

          case SatelliteStatusQuery(queryId, satelliteIndex, replyTo) =>
            refs(satelliteIndex) ! Satellite.Command.StatusQuery(queryId, responseMapper)
            Behaviors.same

          case WrappedSatelliteResponse(wrapped) =>
            wrapped match
              case StatusResponse(queryId, satelliteIndex, status) =>
                inProgress(queryId) ! SatelliteResponse(queryId, satelliteIndex, status)

                active(inProgress - queryId)
        }
      }

      active(Map.empty)
    })
  }
}
