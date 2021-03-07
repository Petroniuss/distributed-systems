import scala.concurrent.Await
import scala.concurrent.duration.Duration
import monix.execution.Scheduler.Implicits.global
import server.Server

object Main {
  def main(args: Array[String]): Unit = {
    val future = Server().runToFuture
    Await.result(future, Duration.Inf)
  }
}
