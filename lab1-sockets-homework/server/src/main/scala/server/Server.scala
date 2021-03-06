package server

import monix.eval.Task
import monix.execution.Scheduler
import server.logger.Logger
import server.message.Message
import server.message._

import java.io.{BufferedInputStream, BufferedReader, IOException, InputStreamReader, PrintWriter}
import java.net.{DatagramPacket, InetAddress, ServerSocket, Socket}
import java.nio.ByteBuffer
import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}
import java.util.Queue
import scala.collection.mutable
import scala.concurrent.{Future, Promise, blocking}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

type ClientMap = mutable.Map[String, Socket]
type MessageQueueElement = (Message, Socket)
type MessageQueue = LinkedBlockingQueue[MessageQueueElement]

object Server {
  val Port = 10231
  val BufferCapacity = 2048
  val io = Scheduler.io("server-io-thread")
  
  def scheduleBlockingIO[T](blockingTask: Task[T]): Task[T] = {
    blockingTask.executeOn(io)
  }
  
  def apply(): Task[Unit] = {
    val messageQueue = new LinkedBlockingQueue[MessageQueueElement]
    val clientMap = new ConcurrentHashMap[String, Socket].asScala
    val dispatchTask = DispatchTask(messageQueue, clientMap)
    val acceptTask = AcceptTCPConnectionTask(messageQueue)
    Task.parSequenceUnordered(dispatchTask :: acceptTask :: Nil) >> Task.unit
  }
}

/**
 * Accepts incoming TCP Connections and spawns new IO threads hadnling these connections
 */
object AcceptTCPConnectionTask {
  def apply(messageQueue: MessageQueue): Task[Unit] = {
    val task = new AcceptTCPConnectionTask(messageQueue).task()
    Server.scheduleBlockingIO(task)
  }
}

case class AcceptTCPConnectionTask(messageQueue: MessageQueue) {
  import Server._ 
  
  def task(): Task[Unit] = {
    for 
      serverSokcet <- createServerSocket()
      _ <- acceptTask(serverSokcet)
    yield ()
  }
  
  def createServerSocket(): Task[ServerSocket] = Task {
    try
      new ServerSocket(Port)
    catch
      case e: IOException => 
        Logger.logError(s"Port ${Port} is taken! Cannot create a tcp socket!")
        throw e
  }

  def acceptTask(serverSocket: ServerSocket): Task[Unit] = {
    val socket = serverSocket.accept() 
    val receiveTask = ReceiveTCPStreamTask(messageQueue, socket)
    val acceptConnectionTask = acceptTask(serverSocket)
    Task.parSequenceUnordered(receiveTask :: acceptConnectionTask :: Nil) >> Task.unit
  }
}

/**
 * Parses incoming messages from client and appends them to dispatch queue.
 */
object ReceiveTCPStreamTask {
  def apply(messageQueue: MessageQueue, socket: Socket): Task[Unit] = {
    val task = new ReceiveTCPStreamTask(messageQueue, socket).receiveTask()
    Server.scheduleBlockingIO(task)
  }
}

case class ReceiveTCPStreamTask(messageQueue: MessageQueue, socket: Socket) {
  import Server._
  val in = socket.getInputStream

  def receiveTask(): Task[Unit] = {
    readMessage().flatMap(option => {
      option match 
        case Some(msg) => 
          Logger.log(s"Received message: $msg!")
          messageQueue.put((msg, socket))
          receiveTask()
        case None => Task.unit
    })
  }
  
  def readMessage(): Task[Option[Message]] = Task {
    try
      Message(in)
    catch
      case e: IOException =>
        socket.close()
        Logger.logError("Connection has been closed!")
        None
  }
}

/**
 * Handles each message received by any of the io threads.
 */
object DispatchTask {
  def apply(messageQueue: MessageQueue, clientMap: ClientMap): Task[Unit] = {
    val task = new DispatchTask(messageQueue, clientMap).dispatchTask()
    Server.scheduleBlockingIO(task)
  }
}

case class DispatchTask(messageQueue: MessageQueue, clientMap: ClientMap) {
  def dispatchTask(): Task[Unit] = {
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
    clientMap += joinMessage.senderId() -> socket
    sendToAll(joinMessage)
  }
  
  def handleByeMsg(byeMessage: ByeMessage): Unit = {
    clientMap -= byeMessage.senderId() 
    sendToAll(byeMessage)
  }
  
  def handleChatMessage(chatMessage: ChatMessage): Unit = {
    sendToAll(chatMessage) 
  }
  
  def sendToAll(message: Message): Unit = {
    val senderNick = message.senderId()
    for (nick, socket) <- clientMap do
      if senderNick != nick then 
        val out = socket.getOutputStream
        out.write(message.encode())
        out.flush()
  }
  
}