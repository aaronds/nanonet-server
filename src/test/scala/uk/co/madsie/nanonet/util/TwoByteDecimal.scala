package test.scala.uk.co.madsie.nanonet.util

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}
import uk.co.madsie.nanonet.util.TwoByteDecimal

import org.w3c.dom._
import javax.xml.parsers._
import javax.xml.xpath._

class TwoByteDecimalTest extends JUnit4(TwoByteDecimalSpec)

object TwoByteDecimalTestRunner extends ConsoleRunner(TwoByteDecimalSpec)

object TwoByteDecimalSpec extends Specification {

	"Two Byte Decimal" should {

		"Use the most sigificant byte of the first byte to determin sign" in {
			val bytes = Array[Byte](-10,0)

			TwoByteDecimal.decode(bytes) must beLessThan(0.0)
		}

		"Produce positive values when the first byte is not set" in {
			val bytes = Array[Byte](10,0)
			TwoByteDecimal.decode(bytes) must beGreaterThan(0.0)
		}

		"handle decimal numbers ok" in {
			val numbers = for(i <- 0 to 7) yield (1.0 / (2 << i),Array[Byte](0,(128 >> i).toByte))

			val answers = numbers.map((num) => (num._1 - TwoByteDecimal.decode(num._2)).abs)

			val totalError = answers.reduceLeft(_ + _)

			totalError must beLessThan(0.0001)
		}
	}
}

// vim: set ts=4 sw=4 et:
