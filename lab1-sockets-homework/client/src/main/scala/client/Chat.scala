package client

import client.message.{ByeMessage, ChatMessage, JoinMessage, Message}
import monix.eval.Task

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

case class Chat() {
  val terminalReader = TerminalReader()
  val terminalWriter = TerminalWriter()
  
  terminalWriter.intro()
  val nick = terminalReader.getLine()
  terminalWriter.welcome(nick)
  
  val future = TCPClient(nick, terminalWriter, terminalReader)
  Await.result(future, Duration.Inf)
}

sealed trait Command 

final case class SendMessage(message: String) extends Command
final case class SendASCIIArt() extends Command
final case class LeaveChat() extends Command

case class TerminalReader() {
  
  def getLine(): String = {
    StdIn.readLine()
  }
  
  def parseCommand(): Task[Command] = Task {
    println("Reading messages!")
    val line = StdIn.readLine()
    line match {
      case leaveString if leaveString.matches("!leave") => LeaveChat()
      case asciiString if asciiString.matches("!ascii") => SendASCIIArt()
      case messageString => SendMessage(messageString)
    }
  }
}

case class TerminalWriter() {
  
  def intro(): Unit = {
    val color = Console.RED
    val reset = Console.RESET
    println(
      s"""${color}***                    Hello                ***${reset}
         | To join the chat type in your nick! 
         | You may use additional commands:
         |    !leave - to leave the chat  
         |    !ascii - to send ascii art
         |""".stripMargin)
  }
  
  def welcome(nick: String): Unit = {
    val color = Console.YELLOW
    val reset = Console.RESET
    println(
      s"""${color}***                    Welcome ${nick}                ***${reset}
         |""".stripMargin)
    
  }
  
  
  def writeMessage(received: Message): Unit = {
    received match
      case joinMessage: JoinMessage => writeFormatted(joinMessage.nick, "joined!")
      case byeMessage:  ByeMessage  => writeFormatted(byeMessage.nick, "left!")
      case chatMessage: ChatMessage => writeFormatted(chatMessage.nick, chatMessage.message)
  }
  
  def writeCommand(command: Command): Unit = {
    command match 
      case SendMessage(message) => 
        writeFormatted("me", message)
      case SendASCIIArt() => 
        writeFormatted("info", "!ascii")
      case LeaveChat() => 
        writeInfo("!left")
        writeFormatted("me", "left!")
  }
  
  def writeInfo(info: String): Unit = {
    writeFormatted("info", info) 
  }
  
  def writeFormatted(who: String, what: String): Unit = {
    val color = pickColor(who)
    val reset = Console.RESET
    println(s"${color}who${reset}> ${what}")
  }
  
  def pickColor(who: String): String = {
    who match
      case me if me == "me" => Console.GREEN
      case info if info == "info" => Console.BLUE
      case other => Console.YELLOW
  }
}