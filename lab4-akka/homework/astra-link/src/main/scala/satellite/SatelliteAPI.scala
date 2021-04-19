package satellite

import scala.util.Random
import satellite.Status

object SatelliteAPI {
  val rand = new Random()

  def getStatus(satelliteIndex: Int): Status = {
    try
      Thread.sleep(100 + rand.nextInt(400))
    catch
      case _: InterruptedException => ()

    val p = rand.nextDouble()
    if p < .8 then
      Status.OK
    else if p < .9 then
      Status.BATTER_LOW
    else if p < .95 then
      Status.NAVIGATION_ERROR
    else
      Status.PROPULSION_ERROR
  }
}

