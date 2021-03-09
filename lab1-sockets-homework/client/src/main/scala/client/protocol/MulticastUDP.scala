package client.protocol

import client.Server
import client.Server.{GroupAddress, MulticastPort}
import event.IncomingMessageEvent
import logger.Logger
import message.Message
import monix.eval.Task
import event.{EventQueue, OutgoingMessageQueue}

import java.io.ByteArrayInputStream
import java.net.{DatagramPacket, InetAddress, InetSocketAddress, MulticastSocket}


case class MulticastClient(nick: String,
                           eventQueue: EventQueue,
                           multicastMsgQueue: OutgoingMessageQueue) {
  import Server._
  val group = InetAddress.getByName(GroupAddress)
  val socketAddress = new InetSocketAddress(MulticastPort)

  def connect(): Task[MulticastSocket] = Task {
    val socket = new MulticastSocket(socketAddress)
    socket.joinGroup(group)
    socket
  }.onErrorHandleWith(ex => 
    Logger.logRed("Failed to join multicast group :/") >> Task.raiseError(ex)
  ) <* Logger.logGreen("Joined multicast group!")

  def readAsync(socket: MulticastSocket): Task[Unit] = {
    val buffer = new Array[Byte](16364)
    val log = Logger.logYellow("Async multicast-reader is up!")
    
    def handleMsg(option: Option[Message]): Unit = {
      option match 
        case Some(msg) =>
          eventQueue.add(IncomingMessageEvent(msg)); ()
        case None => ()
    }

    def readTask(): Task[Unit] = Task {
      val packet = new DatagramPacket(buffer, buffer.length)
      socket.receive(packet)
      val in = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())
      val option = Message(in)
      handleMsg(option)
    }.loopForever

    log >> readTask()
  }

  def writeAsync(socket: MulticastSocket): Task[Unit] = {
    val log = Logger.logRed("Async udp-writer ready!")
    def writeTask(): Task[Unit] = Task {
      val message = multicastMsgQueue.take()
      val encoded = message.encode()
      val packet = new DatagramPacket(encoded, encoded.length, group, MulticastPort)
      socket.send(packet)
    }.loopForever

    log >> writeTask()
  }

}

