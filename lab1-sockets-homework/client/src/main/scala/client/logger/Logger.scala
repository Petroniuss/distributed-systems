package client.logger

object Logger {
  
  def log(msg: String): Unit = {
    println(Thread.currentThread().getName + ": " + msg + Console.RESET)
  }
  
  def logWarning(msg: String): Unit = {
    println(Console.YELLOW + Thread.currentThread().getName + ": " + msg + Console.RESET) 
  }
  
  def logError(msg: String): Unit = {
    println(Console.RED + Thread.currentThread().getName + ": " + msg + Console.RESET)
  }
  
  def logSuccess(msg: String): Unit = {
    println(Console.GREEN + Thread.currentThread().getName + ": " + msg + Console.RESET)
  }
  
}
