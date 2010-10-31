package test.scala.uk.co.madsie.nanonet.util
import org.w3c.dom._

class ChildC(val i : Int) extends ParentClass {

	def this(el : Element,i : Integer) = {this(i.intValue())}
}

// vim: set ts=4 sw=4 et:
