package test.scala.uk.co.madsie.nanonet.util

import org.w3c.dom._

class ChildB(val name : String)  extends ParentClass {

	def this(el : Element){
		this("Child B")
	}

}
// vim: set ts=4 sw=4 et:
