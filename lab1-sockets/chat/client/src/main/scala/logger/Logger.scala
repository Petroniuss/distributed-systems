package logger

import monix.eval.Task

object Logger {
  
  def log(msg: String): Task[Unit] = {
    logF(msg, Console.BOLD)
  }
  
  def logYellow(msg: String): Task[Unit] = {
    logF(msg, Console.YELLOW)
  }
  
  def logRed(msg: String): Task[Unit] = {
    logF(msg, Console.RED)
  }
  
  def logGreen(msg: String): Task[Unit] = {
    logF(msg, Console.GREEN)
  }
  
  def logF(msg: String, color: String): Task[Unit] = Task {
    val reset = Console.RESET
    val thread = Thread.currentThread().getName
//    println(s"${color}[${thread}]${reset} - $msg")
  }
}
