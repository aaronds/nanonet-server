package test.scala.uk.co.madsie.nanonet.server

import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server._

class NanoTestPort(number : Byte,name : String) extends NanoPort(number,name) {

	var serviceCalled = false

	override def service(server : NanoServer,host : NanoHost,packet : NanoPacket) : Unit = {
		serviceCalled = true
	}
}

// vim: set ts=4 sw=4 et:
