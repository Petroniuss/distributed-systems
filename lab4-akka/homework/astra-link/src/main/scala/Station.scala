import Station.{Command, Props, Query, Response, State}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

// todo simplify names
// todo timers and query results gathering
// todo priniting results
object Station {
  sealed trait Response
  case class QueryResult(queryId: String,
                         errors: Map[Int, Status],
                         inTimePercentage: Double) extends Response

  sealed trait Command
  case class Query(queryId: String,
                   firstSatelliteId: Int,
                   range: Int,
                   timeout: Long) extends Command

  case class SatelliteResponse(queryId: String, status: Status) extends Command

  case class Props(name: String)

  case class State(pending: Map[String, QueryState])

  case class QueryState(queryId: String,
                        firstSatelliteId: Int,
                        range: Int,
                        timeout: Long,
                        responseCounter: Int,
                        errors: Map[Int, Status])


  def apply(name: String): Behavior[Command] = {
    val props = Props(name)
    val mempty = State(Map.empty)
    new Station(props).station(mempty)
  }
}

case class Station(props: Props) {
  def station(state: State): Behavior[Command] = {
    Behaviors.receiveMessage {
      case Query(queryId, firstSatelliteId, range, timeout) =>
        Range(firstSatelliteId, range).toList.map(idx => {
          ???
        })
        Behaviors.same
    }
  }
}
