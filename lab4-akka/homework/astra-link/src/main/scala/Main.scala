import akka.NotUsed
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior, DispatcherSelector, PostStop, Signal}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.Random

object Supervisor {
  def apply(): Behavior[NotUsed] =
    Behaviors.setup(context => new Supervisor(context))
}

class Supervisor(context: ActorContext[NotUsed]) extends AbstractBehavior[NotUsed](context) {
//  implicit val executionContext: ExecutionContext =
//    context.system.dispatchers.lookup(DispatcherSelector.fromConfig("src/main/resources/application.conf"))
//
//  println(executionContext)

  val dispatcher = context.spawn(Dispatcher(), "Dispatcher")

  val stationAlphaName = "station-alpha"
  val stationAlpha = context.spawn(Station(stationAlphaName, dispatcher), stationAlphaName)

  val stationBetaName = "station-beta"
  val stationBeta = context.spawn(Station(stationBetaName, dispatcher), stationBetaName)

  val stationEpsilonName = "station-epsilon"
  val stationEpsilon = context.spawn(Station(stationEpsilonName, dispatcher), stationEpsilonName)

  val query1 = Station.Query("1", 100 + Random.nextInt(50), range = 50, timeout = 30000)
  val query2 = Station.Query("2", 100, range = 100, timeout = 300)

  stationAlpha ! query1

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


