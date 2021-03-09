package client

import event._
import logger.Logger
import message.{ChatMessage, JoinMessage, Message}
import client.{Server, TCPClient}
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
      
      Logger.logGreen("TCP client!") >> Task.parSequenceUnordered(taskSeq).executeOn(io) >> Task.unit
    })
  }
  
  def UDP(): Task[Unit] = {
    val udpClient = UDPClient(nick, eventQueue, udpMsgQueue)
    udpClient.connect().flatMap(socket => {
      val readAsync = udpClient.readAsync(socket)
      val writeAsync = udpClient.writeAsync(socket)
      val taskSeq = readAsync :: writeAsync :: Nil

      Logger.logGreen("UDP client!") >> Task.parSequenceUnordered(taskSeq).executeOn(io) >> Task.unit
    })
  }
  
  def Multicast(): Task[Unit] = {
    val multicastClient = MulticastClient(nick, eventQueue, multicastMsgQueue)
    multicastClient.connect().flatMap(socket => {
      val readAsync = multicastClient.readAsync(socket)
      val writeAsync = multicastClient.writeAsync(socket)
      val taskSeq = readAsync :: writeAsync :: Nil

      Logger.logGreen("Multicast client!") >> Task.parSequenceUnordered(taskSeq).executeOn(io) >> Task.unit
    })
  }
}

case class TCPClient(nick: String, 
                     eventQueue: EventQueue, 
                     outgoingMessageQueue: OutgoingMessageQueue) {
  
  def connect(): Task[Socket] = Task {
    try
      val socket = new Socket("localhost", Server.Port)
      val joinMessageBytes = JoinMessage(nick).encode()
      socket.getOutputStream.write(joinMessageBytes)
      socket
    catch
      case exception: IOException =>
        Logger.logRed("Failed to connect to the server :/")
        throw exception
  } <* Logger.logGreen("TCP Socket created!")
  
  def readAsync(socket: Socket): Task[Unit] = {
    val in = socket.getInputStream
    def readTask(): Task[Unit] = Task {
      try
        Message(in).map(msg => IncomingMessageEvent(msg)) match 
          case Some(event) => 
            eventQueue.add(event); ()
          case None => ()
      catch
        case exception: IOException =>
          Logger.logRed("Connection has been closed!")
          throw exception
    } >> readTask()
    
    Logger.logYellow("Async tcp-reader is up!") >> readTask()
  }
  
  def writeAsync(socket: Socket): Task[Unit] = {
    val out = socket.getOutputStream
    def writeTask(): Task[Unit] = Task {
      val message = outgoingMessageQueue.take()
      val encoded = message.encode()
      out.write(encoded)
    }.loopForever 
    
    Logger.logRed("Async tcp-writer ready!") >> writeTask()
  }
  
}

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
    
    def readTask(): Task[Unit] = Task {
      val packet = new DatagramPacket(buffer, buffer.length)
      socket.receive(packet)
      val in = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())
      Message(in).map(msg => IncomingMessageEvent(msg)) match
        case Some(event) =>
          eventQueue.add(event); ()
        case None => ()
    }.loopForever 

    Logger.logYellow("Async udp-reader is up!") >> readTask()
  }

  def writeAsync(socket: DatagramSocket): Task[Unit] = {
    def writeTask(): Task[Unit] = Task {
      val message = outgoingMessageQueue.take()
      val encoded = message.encode()
      val packet = new DatagramPacket(encoded, encoded.length, addr, port)
      socket.send(packet)
    }.onErrorHandleWith(ex => Logger.logRed(ex.toString)).loopForever

    Logger.logRed("Async udp-writer ready!") >> writeTask()
  }

}


case class MulticastClient(nick: String,
                     eventQueue: EventQueue,
                     multicastMsgQueue: OutgoingMessageQueue) {
  import Server._
  val group: InetAddress = InetAddress.getByName(GroupAddress)
  val socketAddress = new InetSocketAddress(MulticastPort)

  def connect(): Task[MulticastSocket] = Task {
    val socket = new MulticastSocket(socketAddress)
    socket.setReuseAddress(true)
    socket.joinGroup(group)
    socket
  }.onErrorHandleWith(ex => {
    Logger.logRed("Failed to join to multicast group :/") >> Task.raiseError(ex)
  }) <* Logger.logGreen("Joined to multicast group!")

  def readAsync(socket: MulticastSocket): Task[Unit] = {
    val buffer = new Array[Byte](16364)

    def readTask(): Task[Unit] = Task {
      val packet = new DatagramPacket(buffer, buffer.length)
      socket.receive(packet)
      val in = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength())
      Message(in).map(msg => IncomingMessageEvent(msg)) match
        case Some(event) =>
          eventQueue.add(event); ()
        case None => ()
    }.loopForever

    Logger.logYellow("Async multicast-reader is up!") >> readTask()
  }

  def writeAsync(socket: MulticastSocket): Task[Unit] = {
    def writeTask(): Task[Unit] = Task {
      val message = multicastMsgQueue.take()
      val encoded = message.encode()
      val packet = new DatagramPacket(encoded, encoded.length, group, MulticastPort)
      socket.send(packet)
    }.onErrorHandleWith(ex => Logger.log(ex.getMessage) >> Task.raiseError(ex)).loopForever

    Logger.logRed("Async udp-writer ready!") >> writeTask()
  }

}

