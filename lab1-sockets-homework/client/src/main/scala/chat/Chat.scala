package chat

import client.Client
import event.{Event, EventDispatcher}
import message.Message
import monix.eval.Task
import terminal.{TerminalReader, TerminalWriter}

import java.util.concurrent.LinkedBlockingQueue

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
      tcp = client.TCP()
      read = terminalReader.readAsync(eventQueue)
      dispatcher = EventDispatcher(nick, eventQueue, outgoingMessageQueue, terminalWriter)
      dispatch = dispatcher.asyncDispatch()
      seq = tcp :: read :: dispatch :: Nil
      _ <- Task.parSequenceUnordered(seq)
    yield ()
  }

}
