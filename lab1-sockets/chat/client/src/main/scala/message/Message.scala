package message

import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.{ByteBuffer, ByteOrder}
import java.util

/*
 * --- Module API ---
*/

sealed trait Message(nick: String) {
  def encode(): Array[Byte]
  
  final def senderId(): String = nick
}


final case class JoinMessage(nick: String) extends Message(nick) {
  override def encode(): Array[Byte] =
    MessageEncoder.encodeSimpleMessage(JoinMessage.messageTypeId, nick)
}

final case class ByeMessage(nick: String) extends Message(nick) {
  override def encode(): Array[Byte] =
    MessageEncoder.encodeSimpleMessage(ByeMessage.messageTypeId, nick)
}

final case class ChatMessage(nick: String, message: String) extends Message(nick) {
  override def encode(): Array[Byte] =
    MessageEncoder.encodeMessage(ChatMessage.messageTypeId, nick, message)
}


/*
* --- Message Format ---
*     4 bytes - message length (total) - n      | HEADER
*     1 byte  - nick length            - m      | HEADER
*     1 byte  - message type id        - x      | HEADER
*     m bytes - nick 
*     (n - 4 - 1 - 1 - m) = (n - m - 6) bytes for additional content parsed according to message type
*/
object Header {
  val HeaderBytesNumber = 4 + 1 + 1
}

object Message {
  final def apply(in: InputStream): Option[Message] =
    MessageDecoder.receive(in)
    
  def header(nickBytesNumber: Int, contentBytesNumber: Int, messageTypeId: Int): Header = {
    val totalBytesNumber = Header.HeaderBytesNumber + nickBytesNumber + contentBytesNumber 
    Header(totalBytesNumber, nickBytesNumber, messageTypeId)
  }
  
  def header(nickBytesNumber: Int, messageTypeId: Int): Header = {
    header(nickBytesNumber, 0, messageTypeId) 
  }
}

case class Header(totalBytesNumber: Int, nickBytesNumber: Int, messageTypeId: Int) {
  import Header._
  
  val contentBytesNumber = totalBytesNumber - HeaderBytesNumber - nickBytesNumber
  
  val contentOffsetInMessageBody = nickBytesNumber
}

case class HeaderWithRawBody(header: Header, bodyBytes: Array[Byte])



/*
 * --- Message Encoder/Decoder ---
*/
  
val UTF_8 = StandardCharsets.UTF_8.name()

object MessageDecoder {
  
  def receive(in: InputStream): Option[Message] = {
    val messageLengthBytes = in.readNBytes(4)
    val messageLength = ByteBuffer.wrap(messageLengthBytes).getInt
    val remainingHeaderBytes = in.readNBytes(2)
    val headerBytes = messageLengthBytes ++ remainingHeaderBytes

    val bodyBytes = in.readNBytes(messageLength - Header.HeaderBytesNumber)
    decode(headerBytes, bodyBytes)
  }

  def decode(headerBytes: Array[Byte], bodyBytes: Array[Byte]): Option[Message] = {
    val header = decodeHeader(headerBytes)
    val raw = HeaderWithRawBody(header, bodyBytes)
    raw match
      case ChatMessage(chatMessage) => Some(chatMessage)
      case JoinMessage(joinMessage) => Some(joinMessage)
      case ByeMessage(byeMessage)   => Some(byeMessage)
      case _                        => None
  }
  
  def decodeHeader(headerBytes: Array[Byte]): Header = {
    val buffer = ByteBuffer.wrap(headerBytes)

    val messageLength = buffer.getInt
    val nickLength = buffer.get.intValue
    val messageTypeId = buffer.get.intValue
    
    Header(messageLength, nickLength, messageTypeId)
  }
  
  def decodeString(bytes: Array[Byte], offset: Int, length: Int): String = {
    new String(bytes, offset, length, UTF_8)
  }

  def decodeNick(header: Header, bodyBytes: Array[Byte]): String = {
    decodeString(bodyBytes, 0, header.nickBytesNumber)
  }

}


object MessageEncoder {
  
  def encodeMessage(msgTypeId: Int, nick: String, content: String): Array[Byte] = {
    val nickBytes = encodeString(nick)
    val nickBytesNumber = nickBytes.length

    val contentBytes = encodeString(content)
    val contentBytesNumber = contentBytes.length

    val header = Message.header(nickBytesNumber, contentBytesNumber, msgTypeId)
    val headerBytes = encodeHeader(header)

    headerBytes ++ nickBytes ++ contentBytes
  }

  def encodeSimpleMessage(msgTypeId: Int, nick: String): Array[Byte] = {
    encodeMessage(msgTypeId, nick, "")
  }
  
  def encodeString(str: String): Array[Byte] = {
    str.getBytes(UTF_8)    
  }
  
  def encodeInt(int: Int): Array[Byte] = {
    ByteBuffer.allocate(4).putInt(int).array()
  }
  
  def encodeHeader(header: Header): Array[Byte] = {
    ByteBuffer.allocate(Header.HeaderBytesNumber)
      .putInt(header.totalBytesNumber)
      .put(header.nickBytesNumber.toByte)
      .put(header.messageTypeId.toByte)
      .array()
  }
}

object JoinMessage {
  val messageTypeId: Int = 1

  def unapply(raw: HeaderWithRawBody): Option[JoinMessage] = {
    val header = raw.header
    val bodyBytes = raw.bodyBytes
    if header.messageTypeId == JoinMessage.messageTypeId then
      val nick = MessageDecoder.decodeNick(header, bodyBytes)
      Some( JoinMessage(nick) )
    else None
  }
}

object ByeMessage {
  val messageTypeId: Int = 2

  def unapply(raw: HeaderWithRawBody): Option[ByeMessage] = {
    val header = raw.header
    val bodyBytes = raw.bodyBytes
    if header.messageTypeId == ByeMessage.messageTypeId then
      val nick = MessageDecoder.decodeNick(header, bodyBytes)
      Some( ByeMessage(nick) )
    else None
  }
}

object ChatMessage {
  val messageTypeId: Int = 3

  def unapply(raw: HeaderWithRawBody): Option[ChatMessage] = {
    val header = raw.header
    val bodyBytes = raw.bodyBytes
    if header.messageTypeId == ChatMessage.messageTypeId then
      val nick = MessageDecoder.decodeNick(header, bodyBytes)
      val actualMessage = MessageDecoder
        .decodeString(bodyBytes, header.contentOffsetInMessageBody, header.contentBytesNumber)
      Some( ChatMessage(nick, actualMessage) )
    else None
  }
}
