import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import Dispatcher.Command
import Dispatcher.Command.SatelliteStatusQuery
import Dispatcher.Command.WrappedSatelliteResponse
import Satellite.Response._
import Satellite._
import Station.SatelliteResponse

object Dispatcher {
  enum Command {
    case SatelliteStatusQuery(queryId: String, stationIndex: Int, replyTo: ActorRef[SatelliteResponse])

    case WrappedSatelliteResponse(satelliteResponse: Response)
  }

  def apply(): Behavior[Command] = {
    Behaviors.setup(context => {
      val refs = Range(100, 200).toList
        .map(idx => context.spawn(Satellite(idx), s"Satellite-$idx"))

      val responseMapper = context.messageAdapter( response =>
        WrappedSatelliteResponse(response)
      )

      def active(inProgress: Map[String, ActorRef[SatelliteResponse]]): Behavior[Command] = {
        Behaviors.receiveMessage {

          case SatelliteStatusQuery(queryId, stationIndex, replyTo) =>
            refs(stationIndex) ! Satellite.Command.StatusQuery(queryId, responseMapper)
            Behaviors.same

          case WrappedSatelliteResponse(wrapped) =>
            wrapped match
              case StatusResponse(queryId, status) =>
                inProgress(queryId) ! SatelliteResponse(queryId, status)

                active(inProgress - queryId)
        }
      }

      active(Map.empty)
    })
  }
}
