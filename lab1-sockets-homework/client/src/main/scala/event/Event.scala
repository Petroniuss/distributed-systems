package event

import message.{ChatMessage, Message}
import logger.Logger
import terminal.TerminalWriter
import monix.eval.Task

import java.util.concurrent.LinkedBlockingQueue

sealed trait Command
final case class SendMessage(message: String) extends Command
final case class SendASCIIArt() extends Command
final case class LeaveChat() extends Command

sealed trait Event
final case class CommandEvent(command: Command) extends Event
final case class IncomingMessageEvent(message: Message) extends Event

type EventQueue = LinkedBlockingQueue[Event]
type OutgoingMessageQueue = LinkedBlockingQueue[Message]


case class EventDispatcher(nick: String,
                           eventQueue: EventQueue,
                           outgoingMessageQueue: OutgoingMessageQueue,
                           terminalWriter: TerminalWriter) {
  
  def asyncDispatch(): Unit = {
    def dispatch(): Task[Unit] = {
      val event = Task { eventQueue.take() }
      val handle = event.flatMap(event => event match
        case CommandEvent(command) => handleCommand(command)
        case IncomingMessageEvent(message) => handleIncomingMessage(message)
      )
      handle >> dispatch()
    }
    
    Logger.logGreen("Dispatcher running!") >> dispatch()
  }
  
  def handleCommand(command: Command): Task[Unit] = Task {
    command match {
      case SendMessage(message) => 
        val chatMessage = ChatMessage(nick, message)
        outgoingMessageQueue.put(chatMessage)
        ()
      case SendASCIIArt() => ()
      case LeaveChat() => ()
    }
  }
  
  def handleIncomingMessage(received: Message): Task[Unit] = {
    terminalWriter.writeMessage(received) 
  }
}

