package database

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.effect._
import cats.implicits._
import doobie.hikari.HikariTransactor

object H2Db {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  // Resource yielding a transactor configured with a bounded connect EC and an unbounded
  // transaction EC. Everything will be closed and shut down cleanly after use.
  val transactor: Resource[IO, HikariTransactor[IO]] = {
    for
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO]    // our blocking EC
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.h2.Driver",
        "jdbc:h2:file:./h2;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE", // connect URL
        "sa",                                   // username
        "",                                     // password
        ce,                                     // await connection here
        be                                      // execute JDBC operations here
      )
    yield xa
  }

  def initialize(transactor: HikariTransactor[IO]): IO[Int] = {
    SatelliteStatsTable.init().transact(transactor)
  }

  def readStats(satelliteIndex: Int, transactor: HikariTransactor[IO]): IO[SatelliteStats] = {
    SatelliteStatsTable.read(satelliteIndex).unique.transact(transactor)
  }
}
