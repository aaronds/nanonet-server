package test.scala.uk.co.madsie.nanonet.server

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}
import uk.co.madsie.nanonet.server._
import uk.co.madsie.nanonet.util._
import test.scala.uk.co.madsie.nanonet.util.Helper

class NanoHostTest extends JUnit4(NanoHostSpec)

object NanoHostTestRunner extends ConsoleRunner(NanoHostSpec)

object NanoHostSpec extends Specification {

	/*
	 * Most testing will be done with NanoDefaultHost as NanoHost is abstract.
	 */

	def NANO_HOST_SPEC_XML = "target/test-classes/nano-host-spec_constructor.xml"

	"constructor" should {
		"work with explicit parameters" in {
			val host = new NanoDefaultHost(NanoAddress(Array[Byte](0,1,3)),"Test",null,List[NanoPort]());
			host must haveClass[NanoDefaultHost]
		}

		"be constructable from an element" in {
			val elList = Helper.getElementList(NANO_HOST_SPEC_XML,"/root/host")
			val el = elList.head

			val host = new NanoDefaultHost(el)

			host must haveClass[NanoDefaultHost]

			host.address equals NanoAddress(Array[Byte](10,0,0,0,1)) mustEqual true

			host.name mustEqual "Test"

			host.ports must notBeEmpty
		}
	}

	def getDummyServer = new NanoServer("",0,0,NanoAddress(Array[Byte](1,2,3)))

	"service" should {
		"dispatch requests to the correct port" in {
			val correctPort = new NanoTestPort(1,"Correct Port")
			val wrongPort = new NanoTestPort(2,"Wrong Port")
			val host = new NanoDefaultHost(new NanoAddress(Array[Byte](1,2,3)),"Test Default",getDummyServer,List(wrongPort,correctPort))

			val packet = new NanoPacket(localPort = 1)

			host.service(packet)

			correctPort.serviceCalled mustEqual true
			wrongPort.serviceCalled mustEqual false
		}
	}
}

// vim: set ts=4 sw=4 et:
