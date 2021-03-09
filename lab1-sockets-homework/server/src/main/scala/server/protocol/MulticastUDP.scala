package server.protocol

import logger.Logger
import message.Message
import monix.eval.Task

import java.io.ByteArrayInputStream
import java.net.{DatagramPacket, InetAddress, InetSocketAddress, MulticastSocket}
import server.{MessageQueue}
import server.protocol._
import server.Server._


object ListenMulticastUDP {

  def apply(messageQueue: MessageQueue): Task[Unit] = {
    initSocket().flatMap(socket => 
      new ListenMulticastUDP(socket, messageQueue).listen()
    ).onErrorHandleWith(_ => Task.unit)
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

  val buffer = new Array[Byte](16364)

  def listen(): Task[Unit] = {
    val log = Logger.logYellow(s"Listening for packets sent via multicast on ${MulticastPort} port!")
    val listenTask = Task {
      val packet = new DatagramPacket(buffer, buffer.length)
      socket.receive(packet)

      val address = packet.getAddress
      val port = packet.getPort
      val in = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())

      Message(in) match 
        case Some(msg) =>
          messageQueue.put((msg, MulticastUDP()))
        case None => ()
    }.loopForever

    log >> listenTask
  }
}
