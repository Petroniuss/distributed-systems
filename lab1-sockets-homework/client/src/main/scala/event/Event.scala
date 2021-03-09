package event

import message.{ByeMessage, ChatMessage, Message}
import logger.Logger
import terminal.{ASCII, TerminalWriter}
import monix.eval.Task
import terminal.ASCII._

import java.util.concurrent.LinkedBlockingQueue

sealed trait Command
final case class SendMessage(message: String) extends Command
case object SendASCIIArt extends Command
case object LeaveChat extends Command
case object TCPSwitch extends Command
case object UDPSwitch extends Command
case object MulticastSwitch extends Command

sealed trait Event
final case class CommandEvent(command: Command) extends Event
final case class IncomingMessageEvent(message: Message) extends Event

type EventQueue = LinkedBlockingQueue[Event]
type OutgoingMessageQueue = LinkedBlockingQueue[Message]

enum Protocol {
  case TCP, UDP, Multicast
}

case class EventDispatcher(nick: String,
                           eventQueue: EventQueue,
                           tcpMsgQueue: OutgoingMessageQueue,
                           udpMsgQueue: OutgoingMessageQueue,
                           multicastMsgQueue: OutgoingMessageQueue,
                           terminalWriter: TerminalWriter) {
  var protocol = Protocol.TCP
  
  def asyncDispatch(): Task[Unit] = {
    def dispatch(): Task[Unit] = {
      val event = Task { eventQueue.take() }
      event.flatMap(event => event match
        case CommandEvent(command) => handleCommand(command)
        case IncomingMessageEvent(message) => handleIncomingMessage(message)
      )
    }.loopForever
    
    Logger.logGreen("Dispatcher running!") >> dispatch()
  }
  
  def handleCommand(command: Command): Task[Unit] = Task {
    command match {
      case SendMessage(message) => 
        val chatMessage = ChatMessage(nick, message)
        queueMessage(chatMessage)
      case LeaveChat =>
        val leaveMessage = ByeMessage(nick)
        queueMessage(leaveMessage)
      case SendASCIIArt =>
        val asciiMessage = ChatMessage(nick, pickOne())
        queueMessage(asciiMessage)
      case TCPSwitch => 
        protocol = Protocol.TCP
      case UDPSwitch =>
        protocol = Protocol.UDP
      case MulticastSwitch =>
        protocol = Protocol.Multicast
    }
  }
  
  def queueMessage(message: Message): Unit = {
    protocol match {
      case Protocol.TCP => tcpMsgQueue.put(message)
      case Protocol.UDP => udpMsgQueue.put(message)
      case Protocol.Multicast => multicastMsgQueue.put(message)
    }
  }
  
  def handleIncomingMessage(received: Message): Task[Unit] = {
    terminalWriter.writeMessage(received) 
  }
}

