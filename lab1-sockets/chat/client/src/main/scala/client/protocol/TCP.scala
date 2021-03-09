package client.protocol

import client.Server
import client.Server._
import event.IncomingMessageEvent
import logger.Logger
import message.{JoinMessage, Message}
import event.{EventQueue, OutgoingMessageQueue}
import monix.eval.Task

import java.io.IOException
import java.net.Socket


case class TCPClient(nick: String,
                     eventQueue: EventQueue,
                     outgoingMessageQueue: OutgoingMessageQueue) {

  def connect(): Task[Socket] = Task {
    val socket = new Socket("localhost", Server.Port)
    val joinMessageBytes = JoinMessage(nick).encode()
    socket.getOutputStream.write(joinMessageBytes)
    socket
  }.onErrorHandleWith(ex =>
    Logger.logRed("Failed to connect to the server :/") >> Task.raiseError(ex))
    <* Logger.logGreen("TCP Socket created!")

  def readAsync(socket: Socket): Task[Unit] = {
    val log = Logger.logYellow("Async tcp-reader is up!")
    val in = socket.getInputStream

    def handleMsg(option: Option[Message]): Unit = {
      option match
        case Some(msg) =>
          eventQueue.add(IncomingMessageEvent(msg)); ()
        case None => ()
    }

    def readTask(): Task[Unit] = Task {
      val msg = Message(in)
      handleMsg(msg)
    }.onErrorHandleWith(ex => 
      Logger.logRed("Connection has been closed!") >> Task.raiseError(ex)).loopForever 

    log >> readTask()
  }

  def writeAsync(socket: Socket): Task[Unit] = {
    val log = Logger.logRed("Async tcp-writer ready!")
    val out = socket.getOutputStream

    def writeTask(): Task[Unit] = Task {
      val message = outgoingMessageQueue.take()
      val encoded = message.encode()
      out.write(encoded)
    }.loopForever

    log >> writeTask()
  }
}