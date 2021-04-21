import akka.NotUsed
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior, DispatcherSelector, PostStop, Signal, SupervisorStrategy}
import akka.event.slf4j.Logger
import database.H2Db
import dispatcher.Dispatcher
import station.Station

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.Random
import cats.effect.IO
import cats.syntax.all._

object Supervisor {
  def apply(): Behavior[NotUsed] =
    Behaviors.setup(context => new Supervisor(context))
}

class Supervisor(context: ActorContext[NotUsed]) extends AbstractBehavior[NotUsed](context) {
  H2Db.transactor.use { transactor => IO {
    H2Db.initialize(transactor).unsafeRunSync()

    val dispatcher = context.spawn(
      Behaviors.supervise(Dispatcher(transactor))
        .onFailure[Exception](SupervisorStrategy.resume), "dispatcher")

    val stationAlphaName = "station-alpha"
    val stationAlpha = context.spawn(Station(stationAlphaName, dispatcher), stationAlphaName)

    val stationBetaName = "station-beta"
    val stationBeta = context.spawn(Station(stationBetaName, dispatcher), stationBetaName)

    val stationEpsilonName = "station-epsilon"
    val stationEpsilon = context.spawn(Station(stationEpsilonName, dispatcher), stationEpsilonName)

    val query1 = Station.Command.Query("1",
      firstSatelliteIndex = 100 + Random.nextInt(50),
      range = 50,
      timeout = FiniteDuration(300, MILLISECONDS))

    val query2 = Station.Command.Query("2",
      firstSatelliteIndex = 100,
      range = 100,
      timeout = FiniteDuration(300, MILLISECONDS))

    stationAlpha ! query1

    stationBeta ! query2

    Thread.sleep(1000)

    val indices = Range(100, 200).toList.foreach(idx => {
      val statsQuery = Station.Command.StatsQuery(idx)
      stationEpsilon ! statsQuery
    })


  } >> IO.never }.unsafeRunAsyncAndForget()

  override def onMessage(ignored: NotUsed): Behavior[NotUsed] = {
    Behaviors.unhandled
  }

  override def onSignal: PartialFunction[Signal, Behavior[NotUsed]] = {
    case PostStop =>
      context.log.info("Application has been stopped.")
      this
  }

}

@main def hello(): Unit = {
 val system = ActorSystem[NotUsed](Supervisor(), "astra-link-system")
}


