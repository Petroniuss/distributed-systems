package client

import event._
import logger.Logger
import message.{ChatMessage, JoinMessage, Message}
import client.{Server, TCPClient}
import monix.eval.Task
import monix.execution.{CancelableFuture, Scheduler}

import java.io.IOException
import java.net.{Socket, UnknownHostException}
import java.util.concurrent.LinkedBlockingQueue
import scala.concurrent.Future

object Server {
  val Port = 10232
}

case class Client(nick: String,
                  eventQueue: EventQueue,
                  outgoingMessageQueue: OutgoingMessageQueue) {
  val io = Scheduler.io("client-io-thread")
  
  def TCP(): Task[Unit] = {
    val tcpClient = TCPClient(nick, eventQueue, outgoingMessageQueue)
    tcpClient.connect().flatMap(socket => {
      val readAsync = tcpClient.readAsync(socket)
      val writeAsync = tcpClient.writeAsync(socket)      
      val taskSeq = readAsync :: writeAsync :: Nil
      
      Logger.logGreen("Connected to the server!") >> Task.parSequenceUnordered(taskSeq).executeOn(io) >> Task.unit
    })
  }
}

case class TCPClient(nick: String, 
                     eventQueue: EventQueue, 
                     outgoingMessageQueue: OutgoingMessageQueue) {
  
  def connect(): Task[Socket] = Logger.logYellow("why doesn't this work?") >> Task {
    try
      val socket = new Socket("localhost", Server.Port)
      val joinMessageBytes = JoinMessage(nick).encode()
      socket.getOutputStream.write(joinMessageBytes)
      socket
    catch
      case exception: IOException =>
        Logger.logRed("Failed to connect to the server :/")
        throw exception
  } <* Logger.logGreen("Socket created!")
  
  def readAsync(socket: Socket): Task[Unit] = {
    val in = socket.getInputStream
    def readTask(): Task[Unit] = Task {
      try
        Message(in).map(msg => IncomingMessageEvent(msg)) match 
          case Some(event) => 
            eventQueue.add(event); ()
          case None => ()
      catch
        case exception: IOException =>
          Logger.logRed("Connection has been closed!")
          throw exception
    } >> readTask()
    
    Logger.logYellow("Async reader is up!") >> readTask()
  }
  
  def writeAsync(socket: Socket): Task[Unit] = {
    val out = socket.getOutputStream
    def writeTask(): Task[Unit] = Task {
      val message = outgoingMessageQueue.take()
      val encoded = message.encode()
      out.write(encoded)
    } >> writeTask()
    
    Logger.logRed("Async writer ready!") >> writeTask()
  }
  
}
