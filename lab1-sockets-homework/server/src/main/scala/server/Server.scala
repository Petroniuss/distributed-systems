package server

import dispatcher.DispatchTask
import logger.Logger
import message.{Message, _}
import monix.eval.Task
import monix.execution.Scheduler
import server.Server.Port

import java.io.{ByteArrayInputStream, DataInputStream, IOException}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, InetSocketAddress, MulticastSocket, NetworkInterface, ServerSocket, Socket, SocketAddress}
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

case class MulticastUDP() extends Protocol {
  override def toString(): String = "Multicast-UDP"
}


type QElement = (Message, Protocol)
type TCPConnectionMap = mutable.Map[String, TCP]
type UDPPConnectionMap = mutable.Map[String, UDP]
type MessageQueue = LinkedBlockingQueue[QElement]

object Server {
  val Port = 10232
  val MulticastPort = 10323
  val GroupAddress = "230.0.0.0"

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
    val listenMulticast = ListenMulticastUDP(messageQueue)
    val tasks = dispatch :: listenTcp :: listenUdp :: listenMulticast :: Nil
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
      _ <- Logger.logGreen(s"Listening on port ${Port} for tcp connections!") 
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

object ListenMulticastUDP {
  import Server._
  
  def apply(messageQueue: MessageQueue): Task[Unit] = {
      initSocket().flatMap(socket => {
        new ListenMulticastUDP(socket, messageQueue).listen()
      }).onErrorHandleWith(_ => Task.unit)
  }
    
  def initSocket(): Task[MulticastSocket] = {
    Task {
      val socketAddress = new InetSocketAddress(MulticastPort)
      val socket = new MulticastSocket(socketAddress)
      socket.setReuseAddress(true)
      
      val group: InetAddress = InetAddress.getByName(GroupAddress)

      socket.joinGroup(group)
      socket
    }.onErrorHandleWith(e => Logger.logRed("Failed to create MulticastSocket :/") >> Task.raiseError(e))
  }
  
}

case class ListenMulticastUDP(socket: MulticastSocket, messageQueue: MessageQueue) {
  import Server._

  val buffer = new Array[Byte](16364)

  def listen(): Task[Unit] = {
    val log = Logger.logYellow(s"Listening for packets sent via multicast on ${MulticastPort} port!")
    val listenTask = Task {
      val packet = new DatagramPacket(buffer, buffer.length)
      socket.receive(packet)

      val address = packet.getAddress
      val port = packet.getPort
      val in = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())

      Message(in) match {
        case Some(msg) =>
          messageQueue.put((msg, MulticastUDP()))
        case None => ()
      }
    }.loopForever

    log >> listenTask
  }
}
