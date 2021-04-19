import Station.{Command, Props, Query, QueryState, Response, SatelliteResponse, State, Timeout}
import akka.actor.TypedActor.self
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import satellite.Status
import Dispatcher._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

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

  case class SatelliteResponse(queryId: String,
                               satelliteIndex: Int,
                               status: Status) extends Command

  case class Timeout(queryId: String) extends Command

  case class Props(name: String, dispatcher: ActorRef[Dispatcher.Command])

  case class State(pending: Map[String, QueryState])

  case class QueryState(query: Query,
                        received: Int,
                        errorResponses: Map[Int, Status],
                        startTimeMs: Long)


  def apply(name: String, dispatcher: ActorRef[Dispatcher.Command]): Behavior[Command] = {
    val props = Props(name, dispatcher)
    val mempty = State(Map.empty)
    new Station(props).station(mempty)
  }
}

// todo single timer per query, after that gather results!
case class Station(props: Props) {
  def station(state: State): Behavior[Command] = {
    Behaviors.receiveMessage {
      case query @ Query(queryId, firstSatelliteId, range, timeout) =>

        // not sure if this self is going to work
        Range(firstSatelliteId, range).toList.foreach(idx => {
          props.dispatcher ! Command.SatelliteStatusQuery(queryId, idx, self)
        })

        val queryState = QueryState( query,
          received = 0,
          errorResponses = Map.empty,
          startTimeMs = System.currentTimeMillis() )

        Behaviors.withTimers { timers =>
          timers.startSingleTimer(Timeout(queryId), FiniteDuration(timeout, TimeUnit.MILLISECONDS))
          this.station(state.copy(state.pending + (queryId -> queryState)))
        }

      case SatelliteResponse(queryId, satelliteIndex, status) =>
        if !state.pending.contains(queryId) then
          Behaviors.same
        else
          val queryState = state.pending(queryId)
          val errors =
            if status.isError() then
              queryState.errorResponses + (satelliteIndex -> status)
            else
              queryState.errorResponses

          val received = queryState.received + 1
          if received == queryState.query.range then
            Behaviors.same
          else
            Behaviors.same
    }
  }


  def showResults(props: Props, queryState: QueryState): Unit = {
    val report = s"""------------- Station [${props.name}] -----------------"
                    |
                    | gatheredResults in
                    |""".stripMargin
    println(report)
  }
}
