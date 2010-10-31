package uk.co.madsie.nanonet.server

import org.w3c.dom._
import uk.co.madsie.nanonet.util._

/**
 * A host, identified by its address.
 *
 * @see NanoDefaultHost
 */

class NanoHost(val address : NanoAddress,val name : String,val server : NanoServer) {

	def this(el : Element,server : NanoServer = null) = {this(NetUtil.getAddressByDottedString(el.getAttribute("address")),el.getAttribute("name"),server)}
	def begin() : Unit = {}
	def service(packet : NanoPacket) : Unit = {}
	def end() : Unit = {}
}

// vim: set ts=4 sw=4 et:
