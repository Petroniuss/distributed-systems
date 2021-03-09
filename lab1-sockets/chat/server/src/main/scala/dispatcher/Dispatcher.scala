package dispatcher

import logger.Logger
import message.{ByeMessage, ChatMessage, JoinMessage, Message}
import monix.eval.Task
import server.protocol.{MulticastUDP, Protocol, TCP, UDP}
import server.{MessageQueue, QElement, TCPConnectionMap, UDPPConnectionMap}

import java.util.concurrent.LinkedBlockingQueue
import scala.collection.mutable
import java.net.{DatagramPacket, DatagramSocket}


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
    }.onErrorHandleWith(e => Logger.logRed(e.getMessage) >> Task.raiseError(e))
      .onErrorRestartIf(_ => true)
      .loopForever

    log >> dispatch()
  }

  def dispatchMessage(message: Message, connection: Protocol): Task[Unit] = {
    message match
      case chatMessage  : ChatMessage => handleChatMessage(chatMessage, connection)
      case joinMsg      : JoinMessage => handleJoinMsg(joinMsg, connection)
      case byeMessage   : ByeMessage  => handleByeMsg(byeMessage, connection)
  }

  def handleJoinMsg(joinMessage: JoinMessage, connection: Protocol): Task[Unit] = {
    val notifyOthers = connection match
      case _: TCP => sendToAll(joinMessage, connection)
      case _ => Task.unit
    
    val log = connection match
      case _: TCP => Logger.logGreen(s"${joinMessage.nick} joined!")
      case _ => Task.unit
    
    for
    _ <- log
    _ <- addConnection(joinMessage.nick, connection)
    _ <- notifyOthers
      yield ()
  }

  def addConnection(nick: String, connection: Protocol): Task[Unit] = Task {
    connection match {
      case udp: UDP => udpConnections += (nick -> udp)
      case tcp: TCP => tcpConnections += (nick -> tcp)
      case _        => ()
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
    connection match 
      case _: UDP =>
        Task.traverse(udpConnections.values)(udp => {
          sendOverUDP(message, udp)
        }) >> Task.unit
      case _: TCP =>
        Task.parTraverse(tcpConnections.values)(tcp => {
          sendOverTCP(message, tcp)
        }) >> Task.unit
      case _: MulticastUDP => Task.unit 
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
