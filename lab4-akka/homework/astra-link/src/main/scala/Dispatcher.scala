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

      val responseMapper = context.messageAdapter[Satellite.Response] (response =>
        WrappedSatelliteResponse(response)
      )

      def active(inProgress: Map[(String, Int), ActorRef[SatelliteResponse]]): Behavior[Command] = {
        Behaviors.receiveMessage {
          case SatelliteStatusQuery(queryId, satelliteIndex, replyTo) =>
            refs(satelliteIndex - 100) ! Satellite.Command.StatusQuery(queryId, responseMapper)
            val key = (queryId, satelliteIndex)
            active( inProgress + (key -> replyTo))

          case WrappedSatelliteResponse(wrapped) =>
            wrapped match
              case StatusResponse(queryId, satelliteIndex, status) =>
                val key = (queryId, satelliteIndex)
                inProgress(key) ! SatelliteResponse(queryId, satelliteIndex, status)
                active( inProgress - key )
        }
      }

      active(Map.empty)
    })
  }
}
