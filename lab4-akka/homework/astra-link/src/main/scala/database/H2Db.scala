package database

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.effect._
import cats.implicits._
import doobie.h2.H2Transactor

object H2Db {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  // A transactor that gets connections from java.sql.DriverManager and executes blocking operations
  // on an our synchronous EC. See the chapter on connection handling for more info.
  def transactor(): Resource[IO, H2Transactor[IO]] = {
    for
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO]    // our blocking EC
      xa <- H2Transactor.newH2Transactor[IO](
        "jdbc:h2:file:./h2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE", // connect URL
        "sa",                                   // username
        "",                                     // password
        ce,                                     // await connection here
        be                                      // execute JDBC operations here
      )
    yield xa
  }

  def run(): IO[ExitCode] = {
    transactor().use { xa =>
      // Construct and run your server here!
      for {
        rows <- SatelliteStatsTable.init().transact(xa)
        _    <- SatelliteStatsTable.update(SatelliteStats(100, 1)).run.transact(xa)
        foo  <- SatelliteStatsTable.read(100).unique.transact(xa)
        _ <- IO(println(foo.reportedErrorsNumber))
      } yield ExitCode.Success

    }
  }


  def apply(): Unit = {
    run().unsafeRunSync()
  }
}
