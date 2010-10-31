package uk.co.madsie.nanonet.service


import uk.co.madsie.nanonet.server._

import java.util.Calendar
import java.util.Date
import org.w3c.dom._
import java.util.GregorianCalendar

/**
 * CTime
 *
 * A NanoPort service to supply the number of seconds since the unix epoc
 */

class CTime(number : Byte,name : String,val yearOnly : Boolean = false) extends NanoPort(number,name) {

	/*
	 * If the year attribute is set only return the time at the begining of this year.
	 */

	def this(el : Element) = this(el.getAttribute("number").toByte,el.getAttribute("name"),el.hasAttribute("year"))

	override def service(server : NanoServer,host : NanoHost,packet : NanoPacket) : Unit = {
		val now = new Date()

		/* Fetch the time */

		val time = if(yearOnly) { 
			val gc = new GregorianCalendar()
			gc.set(gc.get(Calendar.YEAR),0,0,0,0,0)
			gc.getTimeInMillis() / 1000
		}else{
			now.getTime() / 1000
		}

		/* Endiness */

		val bytes = for(i <- 0 to 7) yield ((time >>> (8 * i)) & 255).asInstanceOf[Byte];

		server.send(packet.from,host,packet.remotePort,packet.localPort,bytes.toArray)
	}
}

// vim: set ts=4 sw=4 et:
