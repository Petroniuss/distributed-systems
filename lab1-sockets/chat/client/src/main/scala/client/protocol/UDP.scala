package client.protocol

import client.Server
import client.Server.Port
import event.IncomingMessageEvent
import logger.Logger
import message.{JoinMessage, Message}
import monix.eval.Task

import java.io.ByteArrayInputStream
import java.net.{DatagramPacket, DatagramSocket, InetAddress}
import event.{EventQueue, OutgoingMessageQueue}


case class UDPClient(nick: String,
                     eventQueue: EventQueue,
                     outgoingMessageQueue: OutgoingMessageQueue) {
  import Server._
  val addr = InetAddress.getByName("localhost")
  val port = Port

  def connect(): Task[DatagramSocket] = Task {
    val socket = new DatagramSocket()

    val joinMessageBytes = JoinMessage(nick).encode()
    val packet = new DatagramPacket(joinMessageBytes, joinMessageBytes.length, addr, port)
    socket.send(packet)
    socket
  }.onErrorHandleWith(ex => {
    Logger.logRed("Failed to connect to the server :/") >> Task.raiseError(ex)
  }) <* Logger.logGreen("UDP Socket created!")

  def readAsync(socket: DatagramSocket): Task[Unit] = {
    val buffer = new Array[Byte](16364)
    val log = Logger.logYellow("Async udp-reader is up!")

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
      val msg = Message(in)
      handleMsg(msg)
    }.loopForever

    log >> readTask()
  }

  def writeAsync(socket: DatagramSocket): Task[Unit] = {
    val log = Logger.logRed("Async udp-writer ready!")
    def writeTask(): Task[Unit] = Task {
      val message = outgoingMessageQueue.take()
      val encoded = message.encode()
      val packet = new DatagramPacket(encoded, encoded.length, addr, port)
      socket.send(packet)
    }.onErrorHandleWith(ex => Logger.logRed(ex.toString)).loopForever

    log >> writeTask()
  }

}
