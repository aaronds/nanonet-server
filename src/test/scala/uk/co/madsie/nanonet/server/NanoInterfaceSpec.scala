package test.scala.uk.co.madsie.nanonet.server

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}
import uk.co.madsie.nanonet.server._
import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server.messages._
import scala.actors._,Actor._
import test.scala.uk.co.madsie.nanonet.util.Helper._

class NanoInterfaceTest extends JUnit4(NanoInterfaceSpec)

object NanoInterfaceTestRunner extends ConsoleRunner(NanoInterfaceSpec)

object NanoInterfaceSpec extends Specification {

	"constructor" should {
		"work with explisit parmeters" in {
			val svr = getSvr()
			val inf = new NanoTestInterface(getAddress(),svr)

			inf must haveClass[NanoTestInterface]
		}
	}
}

// vim: set ts=4 sw=4 et:
