package server.protocol

import java.net.{InetAddress, Socket}


sealed trait Protocol
case class UDP(inetAddress: InetAddress, port: Int) extends Protocol {
  override def toString(): String = "UDP"
}
case class TCP(socket: Socket) extends Protocol {
  override def toString(): String = "TCP"
}

case class MulticastUDP() extends Protocol {
  override def toString(): String = "Multicast-UDP"
}
