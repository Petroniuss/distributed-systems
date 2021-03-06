package server

import monix.eval.Task
import server.logger.Logger
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {
  def main(args: Array[String]): Unit = {
    Logger.logWarning("Server is running!")
    val future = Server().runToFuture(Server.io)
    Await.result(future, Duration.Inf)
  }
}
