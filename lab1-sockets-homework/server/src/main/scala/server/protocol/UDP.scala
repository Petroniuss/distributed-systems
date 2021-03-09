package server.protocol

import logger.Logger
import message.Message
import monix.eval.Task
import server.{MessageQueue}
import server.protocol.UDP

import java.io.{ByteArrayInputStream, IOException}
import java.net.{DatagramPacket, DatagramSocket}
import server.Server._


/**
 * Listens to incoming UDP packets.
 */
object ListenUDP {
  def apply(socket: DatagramSocket, messageQueue: MessageQueue): Task[Unit] = {
    new ListenUDP(socket, messageQueue).listen()
  }
}

case class ListenUDP(socket: DatagramSocket, messageQueue: MessageQueue) {
  val buffer = new Array[Byte](16364)

  def listen(): Task[Unit] = {
    val log = Logger.logYellow(s"Listening on $Port for udp-packets!")
    val listenTask = Task {
      val packet = new DatagramPacket(buffer, buffer.length)
      socket.receive(packet)

      val address = packet.getAddress
      val port = packet.getPort
      val in = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())

      Message(in) match 
        case Some(msg) =>
          messageQueue.put((msg, UDP(address, port)))
        case None => ()
    }.loopForever

    log >> listenTask
  }
  
}
