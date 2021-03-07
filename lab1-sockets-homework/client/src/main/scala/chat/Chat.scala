package chat

import client.Client
import event.Event
import message.Message
import monix.eval.Task
import terminal.{TerminalReader, TerminalWriter}

import java.util.concurrent.LinkedBlockingQueue

// todo initialize, get nick and start tcp threads, dispatcher
object Chat {
  val terminalReader = TerminalReader()
  val terminalWriter = TerminalWriter()
  val eventQueue = new LinkedBlockingQueue[Event]()
  val outgoingMessageQueue = new LinkedBlockingQueue[Message]()

  def apply(): Task[Unit] = {
    for  
      _ <- terminalWriter.intro()
      nick <- terminalReader.readLine()
      _ <- terminalWriter.welcome(nick)
      client = Client(nick, eventQueue, outgoingMessageQueue)
      _ <- client.TCP()
    yield ()
  }

}
