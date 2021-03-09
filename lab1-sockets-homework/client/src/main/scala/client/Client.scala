package client

import client.protocol.{MulticastClient, TCPClient, UDPClient}
import event._
import logger.Logger
import message.{ChatMessage, JoinMessage, Message}
import client.{Server}
import monix.eval.Task
import monix.execution.{CancelableFuture, Scheduler}

import java.io.{BufferedInputStream, ByteArrayInputStream, IOException}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, InetSocketAddress, MulticastSocket, Socket, UnknownHostException}
import java.util.concurrent.LinkedBlockingQueue
import scala.concurrent.Future

object Server {
  val Port = 10232
  val MulticastPort = 10323
  val GroupAddress = "230.0.0.0"
}

case class Client(nick: String,
                  eventQueue: EventQueue,
                  tcpMsgQueue: OutgoingMessageQueue,
                  udpMsgQueue: OutgoingMessageQueue,
                  multicastMsgQueue: OutgoingMessageQueue) {
  val io = Scheduler.io("client-io-thread")
  
  def TCP(): Task[Unit] = {
    val tcpClient = TCPClient(nick, eventQueue, tcpMsgQueue)
    tcpClient.connect().flatMap(socket => {
      val readAsync = tcpClient.readAsync(socket)
      val writeAsync = tcpClient.writeAsync(socket)      
      val taskSeq = readAsync :: writeAsync :: Nil
      
      Logger.logGreen("TCP client!") <* Task.parSequenceUnordered(taskSeq).executeOn(io) 
    })
  }
  
  def UDP(): Task[Unit] = {
    val udpClient = UDPClient(nick, eventQueue, udpMsgQueue)
    udpClient.connect().flatMap(socket => {
      val readAsync = udpClient.readAsync(socket)
      val writeAsync = udpClient.writeAsync(socket)
      val taskSeq = readAsync :: writeAsync :: Nil

      Logger.logGreen("UDP client!") <* Task.parSequenceUnordered(taskSeq).executeOn(io) 
    })
  }
  
  def Multicast(): Task[Unit] = {
    val multicastClient = MulticastClient(nick, eventQueue, multicastMsgQueue)
    multicastClient.connect().flatMap(socket => {
      val readAsync = multicastClient.readAsync(socket)
      val writeAsync = multicastClient.writeAsync(socket)
      val taskSeq = readAsync :: writeAsync :: Nil

      Logger.logGreen("Multicast client!") <* Task.parSequenceUnordered(taskSeq).executeOn(io) 
    })
  }
}

  


