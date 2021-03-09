package server.protocol

import logger.Logger
import message.Message
import monix.eval.Task
import server.{MessageQueue}
import server.Server._
import server.protocol._

import java.io.IOException
import java.net.{ServerSocket, Socket}


/**
 * Accepts incoming TCP Connections and spawns new IO threads handling these connections
 */
object ListenTCP {
  def apply(messageQueue: MessageQueue): Task[Unit] = {
    new ListenTCP(messageQueue).listen()
  }
}

case class ListenTCP(messageQueue: MessageQueue) {

  def listen(): Task[Unit] = {
    for
      serverSokcet <- createServerSocket()
      _ <- Logger.logGreen(s"Listening on port ${Port} for tcp connections!")
      _ <- acceptConnections(serverSokcet)
    yield ()
  }


  def acceptConnections(serverSocket: ServerSocket): Task[Unit] = {
    for
      socket <- Task { serverSocket.accept() }
      receiveTask = ReceiveTCPStream(messageQueue, socket).onErrorHandle(_ => ())
      acceptConnectionTask = acceptConnections(serverSocket)
      taskSeq = receiveTask :: acceptConnectionTask :: Nil
      _ <- Task.parSequenceUnordered(taskSeq)
    yield ()
  }

  def createServerSocket(): Task[ServerSocket] = {
    Task { new ServerSocket(Port) }
      .onErrorHandleWith(throwable => 
        Logger.logRed(s"Failed to create socket bound to port: $Port") >> Task.raiseError(throwable)
      )
  }
}


/**
 * Parses incoming messages from client and appends them to dispatch queue.
 */
object ReceiveTCPStream {
  def apply(messageQueue: MessageQueue, socket: Socket): Task[Unit] = {
    new ReceiveTCPStream(messageQueue, socket).receive()
  }
}

case class ReceiveTCPStream(messageQueue: MessageQueue, socket: Socket) {
  val in = socket.getInputStream

  def receive(): Task[Unit] = {
    val log = Logger.logYellow("Receiver listening for messages - up!")
    val receiveTask = receiveMessage()
      .flatMap(handleMessage)
      .loopForever

    log >> receiveTask
  }
  
  def handleMessage(option: Option[Message]): Task[Unit] = {
    option match
      case Some(msg) =>
        Task { messageQueue.put((msg, TCP(socket))) }
      case None =>
        Logger.logYellow("Failed to parse a message!")
  }

  def receiveMessage(): Task[Option[Message]] = {
    Task { Message(in) }.onErrorHandleWith(throwable =>
      Logger.logRed("A connection has been closed!") >> Task.raiseError(throwable)
    )
  }
}
