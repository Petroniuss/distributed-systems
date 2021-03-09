package server

import dispatcher.DispatchTask
import logger.Logger
import message.{Message, _}
import monix.eval.Task
import monix.execution.Scheduler
import server.Server.Port
import server.protocol.{ListenMulticastUDP, ListenTCP, ListenUDP, Protocol, TCP, UDP}

import java.io.{ByteArrayInputStream, DataInputStream, IOException}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, InetSocketAddress,
  MulticastSocket, NetworkInterface, ServerSocket, Socket, SocketAddress}
import java.nio.ByteBuffer
import java.util.Queue
import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}
import scala.collection.mutable
import scala.concurrent.{Future, Promise, blocking}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

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
    val messageQueue = new LinkedBlockingQueue[QElement]
    val tcpConnections = new ConcurrentHashMap[String, TCP].asScala
    val udpConnections = new ConcurrentHashMap[String, UDP].asScala
    val datagramSocket = new DatagramSocket(Port)
    
    val dispatch = DispatchTask(messageQueue, tcpConnections, udpConnections, datagramSocket)
    val listenTcp = ListenTCP(messageQueue)
    val listenUdp = ListenUDP(datagramSocket, messageQueue)
    val listenMulticast = ListenMulticastUDP(messageQueue)
    val tasks = dispatch :: listenTcp :: listenUdp :: listenMulticast :: Nil
    intro() >> Task.parSequenceUnordered(tasks).executeOn(io) >> Task.unit
  }
  
  def intro(): Task[Unit] = Task {
    val color = Console.RED
    val reset = Console.RESET
    println(
      s"""|$color--------------------------------------------------------------------
          |         ***                    Server running..                ***
          |--------------------------------------------------------------------${reset}
          |""".stripMargin)
  }
}




