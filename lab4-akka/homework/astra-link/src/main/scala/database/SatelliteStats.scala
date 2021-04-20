package database

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.effect._
import cats.implicits._
import doobie.h2.H2Transactor

case class SatelliteStats(satelliteIndex: Int,
                          reportedErrorsNumber: Int)


object SatelliteStatsTable {

  val dropSatelliteStatsTable =
    sql"""
        DROP TABLE IF EXISTS satellite_stats
       """.update

  val createSatelliteStatsTable =
    sql"""
        CREATE TABLE satellite_stats (
            id   SERIAL,
            satellite_index INT NOT NULL UNIQUE,
            reported_errors_number INT NOT NULL
        )
        """.update

  val insertDefaultValues = {
    val indices = Range(100, 200).toList
    val errors   = indices.map(_ => 0)

    val stats = indices.zip(errors).map(SatelliteStats.apply)

    val sql = "INSERT INTO satellite_stats (satellite_index, reported_errors_number) VALUES (?, ?)"
    Update[SatelliteStats](sql).updateMany(stats)
  }
      
  def init(): ConnectionIO[Int] = {
    for 
      _ <- dropSatelliteStatsTable.run
      _ <- createSatelliteStatsTable.run
      n <- insertDefaultValues
    yield (n)
  }

  def read(satelliteIndex: Int): Query0[SatelliteStats] = {
    sql"""
      SELECT satellite_index, reported_errors_number 
      FROM satellite_stats
      WHERE satellite_index = $satelliteIndex
    """.query[SatelliteStats]
  }

  def update(satelliteStats: SatelliteStats): Update0 = {
    val idx = satelliteStats.satelliteIndex
    val errors = satelliteStats.reportedErrorsNumber

    sql"""
         UPDATE satellite_stats SET reported_errors_number = $errors WHERE satellite_index = $idx
       """.update
  }

}
