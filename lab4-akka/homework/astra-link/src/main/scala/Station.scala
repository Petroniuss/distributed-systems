import Station.{Command, Props, Query, QueryState, Response, SatelliteResponse, State, Timeout}
import akka.actor.TypedActor.self
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import satellite.Status
import Dispatcher._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

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

  case class Props(name: String, dispatcher: ActorRef[Dispatcher.Command], context: ActorContext[Command])

  case class State(pending: Map[String, QueryState])

  case class QueryState(query: Query,
                        received: Int,
                        errorResponses: Map[Int, Status],
                        startTimeMs: Long)


  def apply(name: String, dispatcher: ActorRef[Dispatcher.Command]): Behavior[Command] = {
    Behaviors.setup { context =>
      val props = Props(name, dispatcher, context)
      val mempty = State(Map.empty)
      new Station(props).station(mempty)
    }
  }
}

case class Station(props: Props) {
  def station(state: State): Behavior[Command] = {
    Behaviors.receiveMessage {
      case query @ Query(queryId, firstSatelliteId, range, timeout) =>

        // not sure if this self is going to work
        Range(firstSatelliteId, firstSatelliteId + range).toList.foreach(idx => {
          props.dispatcher ! Command.SatelliteStatusQuery(queryId, idx, props.context.self)
        })

        val queryState = QueryState( query,
          received = 0,
          errorResponses = Map.empty,
          startTimeMs = System.currentTimeMillis() )

        Behaviors.withTimers { timers =>
          timers.startSingleTimer(Timeout(queryId), FiniteDuration(timeout, TimeUnit.MILLISECONDS))
          this.station(state.copy(state.pending + (queryId -> queryState)))
        }

      case Timeout(queryId) =>
        if !state.pending.contains(queryId) then
          Behaviors.same
        else
          val queryState = state.pending(queryId)
          val received = queryState.received + 1
          showResults(props, queryState.copy(received = received))
          station(State(state.pending - queryId))

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
          val newQueryState = queryState.copy(received = received, errorResponses = errors)
          if received == queryState.query.range then
            showResults(props, newQueryState)
            station(State(state.pending - queryId))
          else
            val newState = State(state.pending + (queryId -> newQueryState))
            station(newState)
    }
  }


  def showResults(props: Props, queryState: QueryState): Unit = {
    val duration = System.currentTimeMillis() - queryState.startTimeMs
    val report = f"""------------- Station [${props.name}] -----------------"
                    |
                    | Gathered results from satellites in ${duration}%d ms.
                    | Errors | In-Time | Total
                    | ${queryState.errorResponses.size}%6d | ${queryState.received}%7d | ${queryState.query.range}%d
                    |""".stripMargin

    val lines = queryState.errorResponses
      .map { case (idx, status) => s" - $idx: $status" }
      .fold("") { (x, y) => x + "\n" + y }

    println(report + lines)
  }
}
