package satellite

enum Status {
  case OK
  case BATTER_LOW
  case PROPULSION_ERROR
  case NAVIGATION_ERROR

  def isError(): Boolean = {
    this == Status.PROPULSION_ERROR || this == Status.NAVIGATION_ERROR
  }
}
