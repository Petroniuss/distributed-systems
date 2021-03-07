import chat.Chat
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {

  def main(args: Array[String]): Unit = {
    val future = Chat().runToFuture
    Await.ready(future, Duration.Inf)
  }

}
