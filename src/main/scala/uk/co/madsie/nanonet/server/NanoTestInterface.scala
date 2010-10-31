package uk.co.madsie.nanonet.server

import uk.co.madsie.nanonet.server.messages._
import uk.co.madsie.nanonet.util._
import concurrent.MailBox
import concurrent.ops._

/**
 * This is a interface for use in tests.
 *
 * A test can simulate a packet arriving by calling NanoTestInterface.injectRecv(NanoPacket)
 * It can then fetch any reponse by NanoTestInterface.extractSend() which returns Option(NanoPacket)
 */

class NanoTestInterface(address : NanoAddress,server : NanoServer = null) extends NanoInterface(address,server) {

	val outbound = new MailBox()

	override def recv(timeout : Long) : NanoPacket = {
		null
	}

	/* Simulate a packet arriving */

	def injectRecv(packet : NanoPacket) : Unit = {
		this ! Receive(packet)
	}

	override def send(p : NanoPacket) : Unit = {
		outbound send p
	}

	/* Fetch an outbound packet for this interface.*/

	def extractSend() : Option[NanoPacket] = {
		outbound.receiveWithin(1000)({
			case p : NanoPacket => Option(p)
			case _ => None
		})
	}

	def end : Unit = {
	}
}

// vim: set ts=4 sw=4 et:
