package test.scala.uk.co.madsie.nanonet.util

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}
import uk.co.madsie.nanonet.util._

class NetUtilTest extends JUnit4(NetUtilSpec)

object NetUtilTestRunner extends ConsoleRunner(NetUtilSpec)

object NetUtilSpec extends Specification {

	"getBytesByDottedString" should {
		"return a string with the correct number of characters" in {
			NetUtil.getAddressByDottedString("10.10.10").address.length must beEqualTo(3)
		}

		"the character values should equal the integer values in the string" in {
			val address = NetUtil.getAddressByDottedString("10.0.1")
			val cmpAddr = new NanoAddress(Array[Byte](10,0,1))
			address equals cmpAddr mustBe true
		}

		"produce values in the range -127 - +128" in {
			val addr = NetUtil.getAddressByDottedString("255.129.255")
			addr.address(0) must beEqualTo(-127)
			addr.address(1).toInt must beLessThan(0)
		}
	}
}

// vim: set ts=4 sw=4 et:
