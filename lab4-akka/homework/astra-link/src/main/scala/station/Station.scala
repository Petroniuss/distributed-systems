package station

import akka.actor.TypedActor.self
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import dispatcher.Dispatcher
import satellite.Status
import station.Station.Command.{Query, QueryResult, StatsQuery, StatsQueryResult}
import station.Station._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object Station {
  enum Command {
    case QueryResult(queryId: String,
                     errorResponses: Map[Int, Status],
                     receivedInTime: Int,
                     duration: FiniteDuration,
                     range: Int)

    case Query(queryId: String,
               firstSatelliteIndex: Int,
               range: Int,
               timeout: FiniteDuration)


    case StatsQuery(satelliteIndex: Int)

    case StatsQueryResult(satelliteIndex: Int, errors: Int)
  }

  case class Props(name: String,
                   dispatcher: ActorRef[Dispatcher.Command],
                   queryResultMapper: ActorRef[Dispatcher.Response.QueryResult],
                   statsQueryResultMapper: ActorRef[Dispatcher.Response.SatelliteStatsQueryResult],
                   context: ActorContext[Command])


  def apply(name: String, dispatcher: ActorRef[Dispatcher.Command]): Behavior[Command] = {
    Behaviors.setup { context =>
      val queryResultMapper = context.messageAdapter[Dispatcher.Response.QueryResult](response =>
        Command.QueryResult(
          queryId = response.queryId,
          errorResponses = response.errors,
          receivedInTime = response.receivedInTime,
          duration = response.duration,
          range = response.range)
      )

      val statsQueryResultMapper = context.messageAdapter[Dispatcher.Response.SatelliteStatsQueryResult](response =>
        Command.StatsQueryResult(response.satelliteIndex, response.errors)
      )

      val props = Props(name, dispatcher, queryResultMapper, statsQueryResultMapper, context)
      new Station(props).receive()
    }
  }

  def constructSatelliteStatusQuery(query: Query, props: Props): Dispatcher.Command.SatelliteStatusQuery = {
    Dispatcher.Command.SatelliteStatusQuery(
      queryId = query.queryId,
      firstSatelliteIndex = query.firstSatelliteIndex,
      range = query.range,
      timeout = query.timeout,
      replyTo = props.queryResultMapper)
  }
}

case class Station(props: Props) {
  def receive(): Behavior[Command] = Behaviors.receiveMessage {
    case query: Query =>
      val dispatcherQuery = constructSatelliteStatusQuery(query, props)
      props.dispatcher ! dispatcherQuery
      Behaviors.same

    case queryResult: QueryResult =>
      showResults(queryResult)
      Behaviors.same

    case StatsQuery(idx) =>
      props.dispatcher ! Dispatcher.Command.SatelliteStatsQuery(idx, props.statsQueryResultMapper)
      Behaviors.same

    case StatsQueryResult(idx, errors) =>
      showSatelliteStats(idx, errors)
      Behaviors.same
  }

  def showSatelliteStats(idx: Int, errors: Int): Unit = {
    if errors > 0 then
      val report = f"""
                      |------------- station.Station [${props.name}] -----------------"
                      |
                      | Satellite-Id | Errors
                      | ${idx}%10d | ${errors}%d
                      |""".stripMargin

      props.context.log.info(report)
  }

  def showResults(queryResult: QueryResult): Unit = {
    val report = f"""
                    |------------- station.Station [${props.name}] -----------------"
                    |
                    | Gathered results from satellites in ${queryResult.duration.toMillis}%d ms.
                    | Errors | In-Time | Total
                    | ${queryResult.errorResponses.size}%6d | ${queryResult.receivedInTime}%7d | ${queryResult.range}%d
                    |""".stripMargin

    val lines = queryResult.errorResponses
      .map { case (idx, status) => s" - $idx: $status" }
      .fold("") { (x, y) => x + "\n" + y }

    props.context.log.info(report + lines)
  }
}
