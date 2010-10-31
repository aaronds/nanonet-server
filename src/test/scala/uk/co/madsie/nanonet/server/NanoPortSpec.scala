package test.scala.uk.co.madsie.nanonet.server

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}
import uk.co.madsie.nanonet.server._
import uk.co.madsie.nanonet.util._
import test.scala.uk.co.madsie.nanonet.util.Helper

class NanoPortTest extends JUnit4(NanoPortSpec)

object NanoPortTestRunner extends ConsoleRunner(NanoPortSpec)

object NanoPortSpec extends Specification {

	/*
	 * Most testing will be done with NanoRestPort as NanoPort is abstract.
	 */

	def NANO_PORT_SPEC_XML = "target/test-classes/nano-port-spec_constructor.xml"

	"constructor" should {

		"work with its explicit parameters" in {
			val port = new NanoRestPort(1,"Test Port","http://www.test.com")
			port must haveClass[NanoRestPort]
		}

		"be constructable from an element" in {
			val elList = Helper.getElementList(NANO_PORT_SPEC_XML,"/root/port")
			val el = elList.head

			val port = new NanoRestPort(el)

			port must haveClass[NanoRestPort]
		}
	}
}

// vim: set ts=4 sw=4 et:
