package terminal

import event.{Command, CommandEvent, EventQueue, LeaveChat, SendASCIIArt, SendMessage, TCPSwitch, UDPSwitch}
import logger.Logger
import message.{ByeMessage, ChatMessage, JoinMessage, Message}
import monix.eval.Task

import scala.io.StdIn

case class TerminalReader() {

  def readAsync(eventQueue: EventQueue): Task[Unit] = {
    val logTask = Logger.logRed("Async terminal-reader up!")
    logTask >> dispatchCommands(eventQueue)
  }

  def dispatchCommands(eventQueue: EventQueue): Task[Unit] = {
    parseCommand()
      .map(command => CommandEvent(command))
      .flatMap(command => Task { eventQueue.put(command) }).loopForever
  }

  def parseCommand(): Task[Command] = Task {
    val line = StdIn.readLine()
    line match
      case udp if udp.matches("!udp") => UDPSwitch()
      case tcp if tcp.matches("!tcp") => TCPSwitch()
      case leaveString if leaveString.matches("!leave") => LeaveChat()
      case asciiString if asciiString.matches("!ascii") => SendASCIIArt()
      case messageString => SendMessage(messageString)
  }

  def readLine(): Task[String] = Task {
    StdIn.readLine()
  }
}


case class TerminalWriter() {

  def intro(): Task[Unit] = Task {
    val color = Console.RED
    val reset = Console.RESET
    println(
      s"""|$color--------------------------------------------------------------------
          |         ***                    Client                ***
          |--------------------------------------------------------------------${reset}
          | To join the chat type in your nick!
          | You may use additional commands:
          |    !leave - to leave the chat
          |    !ascii - to send ascii art
          |    !tcp   - to switch to TCP protocol
          |    !udp   - to switch to UDP protocol
          |""".stripMargin)
  }

  def welcome(nick: String): Task[Unit] = Task {
    val color = Console.YELLOW
    val reset = Console.RESET
    println(
      s"""|$color--------------------------------------------------------------------
         |         ***                    Welcome ${nick}                ***
         |--------------------------------------------------------------------${reset}
         |""".stripMargin)

  }

  def writeMessage(received: Message): Task[Unit] = {
    received match
      case joinMessage: JoinMessage => writeFormatted(joinMessage.nick, "joined!")
      case byeMessage:  ByeMessage  => writeFormatted(byeMessage.nick, "left!")
      case chatMessage: ChatMessage => writeFormatted(chatMessage.nick, chatMessage.message)
  }

  def writeInfo(info: String): Task[Unit] = {
    writeFormatted("info", info)
  }

  def writeFormatted(who: String, what: String): Task[Unit] = Task {
    val color = pickColor(who)
    val reset = Console.RESET
    println(s"${color}${who}${reset}> ${what}")
  }

  def pickColor(who: String): String = {
    who match
      case info if info == "info" => Console.BLUE
      case other => Console.YELLOW
  }

}
