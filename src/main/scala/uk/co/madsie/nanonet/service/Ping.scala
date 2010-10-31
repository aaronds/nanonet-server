package uk.co.madsie.nanonet.service
import org.w3c.dom._
import uk.co.madsie.nanonet.server._

/**
 * Simple ping port service.
 */

class Ping(number : Byte,name : String) extends NanoPort(number,name) {

	def this(el : Element) = this(el.getAttribute("number").toByte,el.getAttribute("name"))

	override def service(server : NanoServer,host : NanoHost,packet : NanoPacket) : Unit = {
		server.send(packet.from,host,packet.remotePort,packet.localPort,packet.data)
	}
}

// vim: set ts=4 sw=4 et:
