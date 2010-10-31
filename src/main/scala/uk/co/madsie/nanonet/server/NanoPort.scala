package uk.co.madsie.nanonet.server

import org.w3c.dom._
import javax.xml.parsers._
import javax.xml.xpath._
import uk.co.madsie.nanonet.util.NodeUtil

/**
 * Abstract port class
 *
 * @see NanoRestPort
 */

abstract class NanoPort(val number : Byte,val name : String){
	def this(el : Element) = {this(el.getAttribute("number").toByte,el.getAttribute("name"))}
	def service(server : NanoServer,host : NanoHost,packet : NanoPacket) : Unit = {}
}

// vim: set ts=4 sw=4 et:
