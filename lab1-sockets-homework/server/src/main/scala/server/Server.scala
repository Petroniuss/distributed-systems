package server

import logger.Logger
import message.{Message, _}
import monix.eval.Task
import monix.execution.Scheduler
import server.Server.Port

import java.io.{ByteArrayInputStream, DataInputStream, IOException}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, ServerSocket, Socket}
import java.nio.ByteBuffer
import java.util.Queue
import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}
import scala.collection.mutable
import scala.concurrent.{Future, Promise, blocking}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

sealed trait Protocol
case class UDP(inetAddress: InetAddress, port: Int) extends Protocol {
  override def toString(): String = "UDP"
}
case class TCP(socket: Socket) extends Protocol {
  override def toString(): String = "TCP"
}


type QElement = (Message, Protocol)
type TCPConnectionMap = mutable.Map[String, TCP]
type UDPPConnectionMap = mutable.Map[String, UDP]
type MessageQueue = LinkedBlockingQueue[QElement]

object Server {
  val Port = 10232
  val io = Scheduler.io("server-io-thread")

  def apply(): Task[Unit] = {
    intro()
    val messageQueue = new LinkedBlockingQueue[QElement]
    val tcpConnections = new ConcurrentHashMap[String, TCP].asScala
    val udpConnections = new ConcurrentHashMap[String, UDP].asScala
    
    val datagramSocket = new DatagramSocket(Port)
    val dispatch = DispatchTask(messageQueue, tcpConnections, udpConnections, datagramSocket)
    val listenTcp = ListenTCP(messageQueue)
    val listenUdp = ListenUDP(datagramSocket, messageQueue)
    val tasks = dispatch :: listenTcp :: listenUdp :: Nil
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
          Task { messageQueue.put((msg, TCP(socket))) }
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

object ListenUDP {
  def apply(socket: DatagramSocket, messageQueue: MessageQueue): Task[Unit] = {
    new ListenUDP(socket, messageQueue).listen()
  }
}

case class ListenUDP(socket: DatagramSocket, messageQueue: MessageQueue) {
  import Server._
  
  val buffer = new Array[Byte](4096)
  
  def listen(): Task[Unit] = { 
    val log = Logger.logYellow(s"Listening on $Port for udp-packets!") 
    val listenTask = Task {
      val packet = new DatagramPacket(buffer, buffer.length)
      socket.receive(packet)
      
      val address = packet.getAddress
      val port = packet.getPort
      val in = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())

      Message(in) match {
        case Some(msg) =>
          messageQueue.put((msg, UDP(address, port)))
        case None => ()
      }
    }.loopForever
    
    log >> listenTask 
  }
}

/**
 * Handles each message received by any of the io threads.
 */
object DispatchTask {
  def apply(messageQueue: MessageQueue, 
            tcpConnections: TCPConnectionMap, udpConnections: UDPPConnectionMap,
            datagramSocket: DatagramSocket): Task[Unit] = {
    new DispatchTask(messageQueue, tcpConnections, udpConnections, datagramSocket).dispatchTask()
  }
}

case class DispatchTask(messageQueue: MessageQueue,
                        tcpConnections: TCPConnectionMap, udpConnections: UDPPConnectionMap,
                        datagramSocket: DatagramSocket) {
  
  def dispatchTask(): Task[Unit] = {
    val log = Logger.logRed("Dispatcher ready to go!")
    def dispatch(): Task[Unit] = {
      for
        popped <- Task { messageQueue.take() }
        (message, conn) = popped
        _ <- dispatchMessage(message, conn)
      yield ()
    }.loopForever

    log >> dispatch()
  }
  
  def dispatchMessage(message: Message, connection: Protocol): Task[Unit] = {
    message match
      case chatMessage  : ChatMessage => handleChatMessage(chatMessage, connection)
      case joinMsg      : JoinMessage => handleJoinMsg(joinMsg, connection)
      case byeMessage   : ByeMessage  => handleByeMsg(byeMessage, connection)
  }
  
  def handleJoinMsg(joinMessage: JoinMessage, connection: Protocol): Task[Unit] = {
    // As we get two join messages it only makes sense to send one to clients..
    val notifyOthers = connection match 
      case UDP(inetAddress, port) => Task.unit
      case TCP(socket) => sendToAll(joinMessage, connection)
    
    for 
      _ <- Logger.logGreen(s"${joinMessage.nick} joined!")
      _ <- addConnection(joinMessage.nick, connection)
      _ <- notifyOthers
    yield ()
  }
  
  def addConnection(nick: String, connection: Protocol): Task[Unit] = Task {
    connection match {
      case udp: UDP => udpConnections += (nick -> udp)
      case tcp: TCP => tcpConnections += (nick -> tcp)
    }
  } >> Task.unit
  
  def removeConnection(nick: String): Task[Unit] = Task {
    val option = tcpConnections.get(nick)
    option match {
      case Some(tcp) =>
        tcpConnections -= nick
        tcp.socket.close()
      case None => ()
    }
    
    udpConnections.remove(nick)
  } >> Task.unit

  def handleByeMsg(byeMessage: ByeMessage, connection: Protocol): Task[Unit] = {
    for 
      _ <- Logger.logYellow(s"${byeMessage.nick} left!") 
      _ <- sendToAll(byeMessage, connection)
    yield ()
  }
  
  def handleChatMessage(chatMessage: ChatMessage, connection: Protocol): Task[Unit] = {
    for 
      _ <- Logger.log(s"${chatMessage.nick}[${connection.toString}]> '${chatMessage.message}'") 
      _ <- sendToAll(chatMessage, connection: Protocol)
    yield ()
  }
  
  def sendToAll(message: Message, connection: Protocol): Task[Unit] = {
    connection match {
      case udp: UDP =>
        Task.parTraverse(udpConnections.values)(udp => {
          sendOverUDP(message, udp)
        }) >> Task.unit
      case TCP(socket) =>
        Task.parTraverse(tcpConnections.values)(tcp => {
          sendOverTCP(message, tcp)
        }) >> Task.unit
    }
  }
  
  def sendOverUDP(message: Message, udp: UDP): Task[Unit] = {
    Task {
      val address = udp.inetAddress
      val port = udp.port;
      val encoded = message.encode()
      val packet = new DatagramPacket(encoded, encoded.length, address, port);
      datagramSocket.send(packet)
    }.onErrorHandleWith(_ => Task {
      removeConnection(message.senderID())
    } >> Logger.logYellow(s"Removed ${message.senderID()} from active connections!"))
    
  }
  
  def sendOverTCP(message: Message, tcp: TCP): Task[Unit] = {
    Task {
      val out = tcp.socket.getOutputStream
      out.write(message.encode())
      out.flush()
    }.onErrorHandleWith(_ => Task {
      removeConnection(message.senderID())
    } >> Logger.logYellow(s"Removed ${message.senderID()} from active connections!"))
  }

}
