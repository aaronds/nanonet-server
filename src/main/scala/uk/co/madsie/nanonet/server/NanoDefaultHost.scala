package uk.co.madsie.nanonet.server

import org.w3c.dom._
import javax.xml.xpath._
import uk.co.madsie.nanonet.util._

object NanoDefaultHost {
	/**
	 * Produce a list of NanoPorts from an element.
	 */

	def getPorts(el : Element) : List[NanoPort] = {
		val xpath = XPathFactory.newInstance.newXPath
		val portEls = NodeUtil.nodeSetToList(xpath.evaluate("port",el,XPathConstants.NODESET));
		NodeUtil.constructObjectsByElement(portEls,classOf[NanoRestPort])
	}
}

/**
 * The default implementation of NanoHost, delegates to a list of port handlers.
 */

class NanoDefaultHost(address : NanoAddress,name : String,server : NanoServer,val ports : List[NanoPort]) extends NanoHost(address,name,server) {

	def this(el : Element,server : NanoServer = null) = this(NetUtil.getAddressByDottedString(el.getAttribute("address")),el.getAttribute("name"),server,NanoDefaultHost.getPorts(el))

	override def service(packet : NanoPacket) : Unit = {
		ports.find(_.number == packet.localPort).foreach(_.service(server,this,packet))
	}
}
// vim: set ts=4 sw=4 et:
