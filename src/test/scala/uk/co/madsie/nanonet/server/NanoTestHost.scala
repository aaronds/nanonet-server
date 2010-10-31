package test.scala.uk.co.madsie.nanonet.server

import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server._

class NanoTestHost(address : NanoAddress,name : String,server : NanoServer) extends NanoHost(address,name,server){

	var beginCalled = false
	var serviceCalled = false
	var endCalled = false

	override def begin() = {
		beginCalled = true
	}

	override def service(packet : NanoPacket) : Unit = {
		serviceCalled = true
	}

	override def end() = {
		endCalled = true
	}
}

// vim: set ts=4 sw=4 et:
