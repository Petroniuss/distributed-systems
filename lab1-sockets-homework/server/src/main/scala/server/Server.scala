package server

import logger.Logger
import message.{Message, _}
import monix.eval.Task
import monix.execution.Scheduler
import server.Server.Port

import java.io.IOException
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
    intro()
    val messageQueue = new LinkedBlockingQueue[MessageQueueElement]
    val clientMap = new ConcurrentHashMap[String, Socket].asScala
    
    val dispatch = DispatchTask(messageQueue, clientMap)
    val listen = ListenTCP(messageQueue)
    val tasks = dispatch :: listen :: Nil
    Task.parSequenceUnordered(tasks).executeOn(io) >> Task.unit
  }
  
  def intro(): Unit = {
    val color = Console.RED
    val reset = Console.RESET
    println(
      s"""|$color--------------------------------------------------------------------
          |         ***                    Server running..                ***
          |--------------------------------------------------------------------${reset}
          |""".stripMargin)
  }
}

/**
 * Accepts incoming TCP Connections and spawns new IO threads hadnling these connections
 */
object ListenTCP {
  def apply(messageQueue: MessageQueue): Task[Unit] = {
    new ListenTCP(messageQueue).listen()
  }
}

case class ListenTCP(messageQueue: MessageQueue) {
  import Server._

  def listen(): Task[Unit] = {
    for
      serverSokcet <- createServerSocket()
      _ <- Logger.logGreen(s"Listening on port ${Port}") 
      _ <- acceptConnections(serverSokcet)
    yield ()
  }


  def acceptConnections(serverSocket: ServerSocket): Task[Unit] = {
    for
      socket <- Task { serverSocket.accept() }
      receiveTask = ReceiveTCPStreamTask(messageQueue, socket).onErrorHandle(_ => ())
      acceptConnectionTask = acceptConnections(serverSocket)
      taskSeq = receiveTask :: acceptConnectionTask :: Nil
      _ <- Task.parSequenceUnordered(taskSeq) 
    yield ()
  }

  def createServerSocket(): Task[ServerSocket] = {
    Task { new ServerSocket(Port) }
      .onErrorRecoverWith(throwable => {
        throwable match 
          case e: IOException =>
            Logger.logRed(s"Failed to create socket bound to port: $Port") >> Task.raiseError(e)
    })
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
    val log = Logger.logYellow("Receiver listening for messages - up!") 
    val receive = readMessage().flatMap(option => {
      option match
        case Some(msg) =>
          Task { messageQueue.put((msg, socket)) }
        case None =>
          Logger.logYellow("Failed to parse a message!") 
    }).loopForever
    
    log >> receive
  }
  
  def readMessage(): Task[Option[Message]] = {
    Task { Message(in) }.onErrorHandleWith(throwable => 
      throwable match 
        case e: Exception =>
          Logger.logRed("A connection has been closed!") >> Task.raiseError(e)
    )
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
    val log = Logger.logRed("Dispatcher ready to go!")
    def dispatch(): Task[Unit] = {
      for
        popped <- Task { messageQueue.take() }
        (message, socket) = popped
        _ <- dispatchMessage(message, socket)
      yield ()
    }.loopForever

    log >> dispatch()
  }
  
  def dispatchMessage(message: Message, socket: Socket): Task[Unit] = {
    message match
      case joinMsg      : JoinMessage => handleJoinMsg(joinMsg, socket)
      case byeMessage   : ByeMessage  => handleByeMsg(byeMessage)
      case chatMessage  : ChatMessage => handleChatMessage(chatMessage)
  }
  
  def handleJoinMsg(joinMessage: JoinMessage, socket: Socket): Task[Unit] = {
    for 
      _ <- Logger.logGreen(s"${joinMessage.nick} joined!")
      _ <- Task { clientMap += joinMessage.nick -> socket } 
      _ <- sendToAll(joinMessage)
    yield ()
  }
  
  def handleByeMsg(byeMessage: ByeMessage): Task[Unit] = {
    for 
      _ <- Logger.logYellow(s"${byeMessage.nick} left!") 
      _ <- sendToAll(byeMessage)
      _ <- Task {
        val option = clientMap.get(byeMessage.nick)
        option match {
          case Some(socket) =>
            clientMap -= byeMessage.nick
            socket.close()
          case None => ()
        }
      }
    yield ()
  }
  
  def handleChatMessage(chatMessage: ChatMessage): Task[Unit] = {
    for 
      _ <- Logger.log(s"${chatMessage.nick} sent message: ${chatMessage.message}!") 
      _ <- sendToAll(chatMessage)
    yield ()
  }
  
  def sendToAll(message: Message): Task[Unit] = {
    val senderNick = message.senderID()
    
    Task.parTraverse(clientMap.toSeq)(tuple => {
      val (nick, socket) = tuple
      send(message, nick, socket)
    }) >> Task.unit
  }
  
  def send(message: Message, nick: String, socket: Socket): Task[Unit] = {
    Task {
      val out = socket.getOutputStream
      out.write(message.encode())
      out.flush()
    }.onErrorHandleWith(_ => Task {
      clientMap.remove(nick)
    } >> Logger.logYellow(s"Removed ${nick} from active connections!"))
  }

}
