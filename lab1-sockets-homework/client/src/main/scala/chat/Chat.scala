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
  val tcpMsgQueue = new LinkedBlockingQueue[Message]()
  val udpMsgQueue = new LinkedBlockingQueue[Message]()
  val multicastMsgQueue = new LinkedBlockingQueue[Message]()

  def apply(): Task[Unit] = {
    for  
      _ <- terminalWriter.intro()
      nick <- terminalReader.readLine()
      _ <- terminalWriter.welcome(nick)
      client = Client(nick, eventQueue, tcpMsgQueue, udpMsgQueue, multicastMsgQueue)
      tcp = client.TCP()
      udp = client.UDP()
      multicast = client.Multicast()
      read = terminalReader.readAsync(eventQueue)
      dispatcher = EventDispatcher(nick, eventQueue, tcpMsgQueue, udpMsgQueue, multicastMsgQueue, terminalWriter)
      dispatch = dispatcher.asyncDispatch()
      seq = tcp :: udp :: read :: multicast :: dispatch :: Nil
      _ <- Task.parSequenceUnordered(seq)
    yield ()
  }

}
