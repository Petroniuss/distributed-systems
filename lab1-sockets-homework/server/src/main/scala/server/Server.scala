package server

import logger.Logger
import message.{Message, _}
import monix.eval.Task
import monix.execution.Scheduler
import server.Server.Port

import java.io._
import java.net.{DatagramPacket, InetAddress, ServerSocket, Socket}
import java.nio.ByteBuffer
import java.util.Queue
import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}
import scala.collection.mutable
import scala.concurrent.{Future, Promise, blocking}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

type ClientMap = mutable.Map[String, Socket]
type MessageQueueElement = (Message, Socket)
type MessageQueue = LinkedBlockingQueue[MessageQueueElement]

object Server {
  val Port = 10232
  val io = Scheduler.io("server-io-thread")
  
  def apply(): Task[Unit] = {
    val messageQueue = new LinkedBlockingQueue[MessageQueueElement]
    val clientMap = new ConcurrentHashMap[String, Socket].asScala
    val dispatchTask = DispatchTask(messageQueue, clientMap)
    val acceptTask = AcceptTCPConnectionTask(messageQueue)
    Task.parSequenceUnordered(dispatchTask :: acceptTask :: Nil).executeOn(io) >> Task.unit
  }
}

/**
 * Accepts incoming TCP Connections and spawns new IO threads hadnling these connections
 */
object AcceptTCPConnectionTask {
  def apply(messageQueue: MessageQueue): Task[Unit] = {
    new AcceptTCPConnectionTask(messageQueue).acceptConnectionTask()
  }
}

case class AcceptTCPConnectionTask(messageQueue: MessageQueue) {
  import Server._
  
  def acceptConnectionTask(): Task[Unit] = {
    for 
      _ <- Task { Logger.logGreen(s"Listening on port ${Port}") }
      serverSokcet <- createServerSocket()
      _ <- acceptTask(serverSokcet)
    yield ()
  }
  
  def sock(serverSocket: ServerSocket): Task[Socket] = {
    Task { serverSocket.accept() }
  }
  
  def acceptTask(serverSocket: ServerSocket): Task[Unit] = {
    sock(serverSocket).flatMap(socket => {
      val receiveTask = ReceiveTCPStreamTask(messageQueue, socket)
      val acceptConnectionTask = acceptTask(serverSocket)
      Task.parSequenceUnordered(receiveTask :: acceptConnectionTask :: Nil) >> Task.unit
    })
  }

  def createServerSocket(): Task[ServerSocket] = Task {
    try
      new ServerSocket(Port)
    catch
      case e: IOException =>
        Logger.logRed(s"Port ${Port} is taken! Cannot create a tcp socket!")
        throw e
  }

}

/**
 * Parses incoming messages from client and appends them to dispatch queue.
 */
object ReceiveTCPStreamTask {
  def apply(messageQueue: MessageQueue, socket: Socket): Task[Unit] = {
    new ReceiveTCPStreamTask(messageQueue, socket).receiveTask()
  }
}

case class ReceiveTCPStreamTask(messageQueue: MessageQueue, socket: Socket) {
  import Server._
  val in = socket.getInputStream

  def receiveTask(): Task[Unit] = {
    Task { Logger.logYellow("Receiver!") } >>
    readMessage().flatMap(option => {
      option match
        case Some(msg) =>
          messageQueue.put((msg, socket))
          receiveTask()
        case None => 
          Logger.logYellow("Failed to parse a message!")
          receiveTask()
    })
  }
  
  def readMessage(): Task[Option[Message]] = Task {
    try
      Message(in)
    catch
      case e: IOException =>
        socket.close()
        Logger.logRed("Connection has been closed!")
        None
  }
}

/**
 * Handles each message received by any of the io threads.
 */
object DispatchTask {
  def apply(messageQueue: MessageQueue, clientMap: ClientMap): Task[Unit] = {
    new DispatchTask(messageQueue, clientMap).dispatchTask()
  }
}

case class DispatchTask(messageQueue: MessageQueue, clientMap: ClientMap) {
  def dispatchTask(): Task[Unit] = {
    Task { Logger.logRed("Dispatcher!") } >>
    Task {
      val (message, socket) = messageQueue.take()
      dispatchMessage(message, socket)
    } >> dispatchTask()
  }
  
  def dispatchMessage(message: Message, socket: Socket): Unit = {
    message match
      case joinMsg      : JoinMessage => handleJoinMsg(joinMsg, socket)
      case byeMessage   : ByeMessage  => handleByeMsg(byeMessage)
      case chatMessage  : ChatMessage => handleChatMessage(chatMessage)
  }
  
  def handleJoinMsg(joinMessage: JoinMessage, socket: Socket): Unit = {
    Logger.logGreen(s"${joinMessage.nick} joined!")
    clientMap += joinMessage.nick -> socket
    sendToAll(joinMessage)
  }
  
  def handleByeMsg(byeMessage: ByeMessage): Unit = {
    Logger.logYellow(s"${byeMessage.nick} left!")
    clientMap -= byeMessage.nick
    sendToAll(byeMessage)
  }
  
  def handleChatMessage(chatMessage: ChatMessage): Unit = {
    Logger.log(s"${chatMessage.nick} sent message: ${chatMessage.message}!")
    sendToAll(chatMessage)
  }
  
  def sendToAll(message: Message): Unit = {
    val senderNick = message.senderID()
    for (nick, socket) <- clientMap do
      if senderNick != nick then
        val out = socket.getOutputStream
        out.write(message.encode())
        out.flush()
  }
  
}
