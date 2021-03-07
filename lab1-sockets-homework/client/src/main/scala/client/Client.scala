package client

import client.logger.Logger
import client.message.{ChatMessage, JoinMessage, Message}
import client.TerminalWriter
import monix.eval.Task
import monix.execution.{CancelableFuture, Scheduler}

import java.io.IOException
import java.net.{Socket, UnknownHostException}
import java.util.concurrent.LinkedBlockingQueue
import scala.concurrent.Future

object Server {
  val Port = 10232
}

object Client {
  val io = Scheduler.io("client-io-thread")

  def scheduleBlockingIO[T](blockingTask: Task[T]): Task[T] = {
    blockingTask.executeOn(io)
  }
}

object TCPClient {
  def apply(nick: String, terminalWriter: TerminalWriter, terminalReader: TerminalReader): CancelableFuture[Unit] = {
    println("creating client")
    try
      val socket = new Socket("localhost", Server.Port)
      val readTask = TCPReader(socket, terminalWriter)
      val writeTask = TCPWriter(socket, nick, terminalReader)
      println("clinet created")
      val parTask = Task.parSequenceUnordered(Task {Logger.log("Hi") }  :: Nil) >> Task.unit
      val tsk = Task {Logger.log("Hi") }
      println("fif")
      tsk.runToFuture(Client.io)
      parTask.runToFuture(Client.io)
    catch
      case e: IOException => 
        println(e)
        throw e
      case e: UnknownHostException => 
        println(e)
        throw e
      case e: SecurityException => 
        println(e)
        throw e
  }
}

//case class TCPClient(nick: String, terminalWriter: TerminalWriter, terminalReader: TerminalReader) {
//}

//object TCPClientConnect {
//  def apply(nick: String): Socket = {
//    new TCPClientConnect(nick).connect()
//  }
//}

//case class TCPClientConnect(nick: String) {
//  def connect(): Socket = {
////    try
//    val socket = new Socket("localhost", Server.Port)
//    val joinMessageBytes = JoinMessage(nick).encode()
////    socket.getOutputStream.write(joinMessageBytes)
//    Logger.logSuccess("Connected to the server!")
//    socket
////    catch
////      case exception: Exception =>
////        Logger.logError("Failed to connect to the server :/")
////        throw exception
//  }
//}

object TCPReader {
  def apply(socket: Socket, terminalWriter: TerminalWriter): Task[Unit] = {
    val task = new TCPReader(socket, terminalWriter).readTask()
    Client.scheduleBlockingIO(task)
  }
  
}

case class TCPReader(socket: Socket, terminalWriter: TerminalWriter) {
  val in = socket.getInputStream
  
  def readTask(): Task[Unit] = {
    for 
      message <- readMessage()
      _       <- Task { terminalWriter.writeMessage(message) }
      _       <- readTask()
    yield () 
  }
  
  def readMessage(): Task[Message] = {
    try
      Message.apply(in) match 
        case Some(message) => Task { message }
        case None => readMessage()
    catch
      case exception: Exception => 
        Logger.logError("Disconnected!")
        throw exception
  }
  
}

type MessageQueue = LinkedBlockingQueue[Message]

object TCPWriter {
  def apply(socket: Socket, nick: String, terminalReader: TerminalReader): Task[Unit] = {
    val task = new TCPWriter(socket, nick, terminalReader).task()
    Logger.log("Schduled writer")
    Client.scheduleBlockingIO(task)
  }
}

case class TCPWriter(socket: Socket, nick: String, terminalReader: TerminalReader) {
  val out = socket.getOutputStream
  
  def task(): Task[Unit] = {
    Task { Logger.logError("hello from writer!") } >>
    terminalReader.parseCommand().flatMap(command =>
      command match {
        case SendMessage(message) => 
          val chatMessage = ChatMessage(nick, message)
          writeTask(chatMessage)
        case SendASCIIArt() => Task.unit
        case LeaveChat() => Task.unit
      }
    ) >> task()
  } 
  
  def writeTask(message: Message): Task[Unit] = {
    Task {
      val bytes = message.encode()
      out.write(bytes)
    } 
  }
  
}