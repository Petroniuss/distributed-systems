package dispatcher

import akka.actor.TypedActor.self
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, DispatcherSelector, SupervisorStrategy}
import dispatcher.Dispatcher.Command.{SatelliteStatusQuery, Timeout, WrappedSatelliteResponse, SatelliteStatsQuery}
import dispatcher.Dispatcher.Response.{SatelliteStatsQueryResult}
import dispatcher.Dispatcher._
import satellite.Satellite.Response.StatusResponse
import satellite.{Satellite, Status}
import database.H2Db
import cats.effect.IO
import doobie.hikari.HikariTransactor

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object Dispatcher {
  val blockingDispatcher = DispatcherSelector.fromConfig("my-blocking-dispatcher")

  enum Command {
    case SatelliteStatusQuery(queryId: String,
                              firstSatelliteIndex: Int,
                              range: Int,
                              timeout: FiniteDuration,
                              replyTo: ActorRef[Response.QueryResult])

    case SatelliteStatsQuery(satelliteIndex: Int, replyTo: ActorRef[Response.SatelliteStatsQueryResult])

    case WrappedSatelliteResponse(satelliteResponse: Satellite.Response)

    case Timeout(queryId: String)
  }

  enum Response {
    case QueryResult(queryId: String,
                     errors: Map[Int, Status],
                     receivedInTime: Int,
                     duration: FiniteDuration,
                     range: Int)

    case SatelliteStatsQueryResult(satelliteIndex: Int,
                                   errors: Int)
  }

  case class State(pending: Map[String, QueryState])

  case class QueryState(query: SatelliteStatusQuery,
                        received: Int,
                        errorResponses: Map[Int, Status],
                        startTimeMs: FiniteDuration)

  def apply(transactor: HikariTransactor[IO]): Behavior[Command] = {
    Behaviors.setup(context => {
      val blockingDispatcher = DispatcherSelector.fromConfig("my-blocking-dispatcher")

      val refs = Range(100, 200)
        .toList
        .map(spawnSatellite(context, _))

      val responseMapper = context.messageAdapter[Satellite.Response](response =>
        WrappedSatelliteResponse(response)
      )

      val mempty = State(Map.empty)
      new Dispatcher(refs, responseMapper, transactor).dispatch(mempty)
    })
  }

  def spawnSatellite(context: ActorContext[Command], satelliteIndex: Int): ActorRef[Satellite.Command] = {
    val supervised = Behaviors
      .supervise(Satellite(satelliteIndex))
      .onFailure[Exception](SupervisorStrategy.resume)

    context.spawn(supervised, s"satellite.Satellite-$satelliteIndex", blockingDispatcher)
  }

  def constructNewQueryState(state: QueryState, satelliteIndex: Int, status: Status): QueryState = {
    val received = state.received + 1
    val errors =
      if status.isError() then
        state.errorResponses + (satelliteIndex -> status)
      else
        state.errorResponses

    state.copy(received = received, errorResponses = errors)
  }

  def constructQueryResult(queryState: QueryState): Dispatcher.Response.QueryResult = {
    val now = FiniteDuration(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
    val duration = now - queryState.startTimeMs
    val range = queryState.query.range
    val queryId = queryState.query.queryId
    val errors = queryState.errorResponses
    val received = queryState.received

    Dispatcher.Response.QueryResult(queryId, errors, received, duration, range)
  }
}

case class Dispatcher(satellites: List[ActorRef[Satellite.Command]],
                      responseMapper: ActorRef[Satellite.Response],
                      transactor: HikariTransactor[IO]) {

  def dispatch(state: State): Behavior[Command] = Behaviors.receiveMessage {
    case query @ SatelliteStatusQuery(queryId, firstSatelliteIndex, range, timeout, replyTo) =>
      Range(firstSatelliteIndex, firstSatelliteIndex + range).toList.foreach(idx => {
        satellites(idx - 100) ! Satellite.Command.StatusQuery(queryId, responseMapper)
      })

      val queryState = QueryState(query,
        received = 0,
        errorResponses = Map.empty,
        startTimeMs = FiniteDuration(System.currentTimeMillis(), TimeUnit.MILLISECONDS))

      Behaviors.withTimers { timers =>
        timers.startSingleTimer(Timeout(queryId), timeout)

        dispatch(state.copy(state.pending + (queryId -> queryState)))
      }

    case WrappedSatelliteResponse(StatusResponse(queryId, satelliteIndex, status)) =>
      val key = (queryId, satelliteIndex)
      if !state.pending.contains(queryId) then
        Behaviors.same
      else
        val oldQueryState = state.pending(queryId)
        val queryState    = constructNewQueryState(oldQueryState, satelliteIndex, status)
        if queryState.received == queryState.query.range then
          val replyTo = queryState.query.replyTo
          val queryResult = constructQueryResult(queryState)

          replyTo ! queryResult
          updateDatabase(queryState.errorResponses)

          dispatch( State(state.pending - queryId) )
        else
          val newState = State(state.pending + (queryId -> queryState))

          dispatch( newState )

    case Timeout(queryId) =>
      if !state.pending.contains(queryId) then
        Behaviors.same
      else
        val oldQueryState = state.pending(queryId)
        val received = oldQueryState.received + 1
        val queryState = oldQueryState.copy(received = received)

        val queryResult = constructQueryResult(queryState)
        val replyTo = queryState.query.replyTo

        replyTo ! queryResult
        updateDatabase(queryState.errorResponses)

        dispatch( State(state.pending - queryId) )

    case SatelliteStatsQuery(satelliteIndex, replyTo) =>
      // run db query async in order not to block the actor
      H2Db.readStats(satelliteIndex, transactor).unsafeRunAsync(either => {
        either match 
          case Left(throwable) => ()
          case Right(dbResult) =>
            val queryResult: SatelliteStatsQueryResult = 
             SatelliteStatsQueryResult(dbResult.satelliteIndex, 
            dbResult.reportedErrorsNumber)

            replyTo ! queryResult
      })

      Behaviors.same
  }


  def updateDatabase(errors: Map[Int, Status]): Unit = {
    val indices = errors.toList.map(_._1)
    H2Db.updateStats(indices, transactor).unsafeRunAsyncAndForget()
  }
}

