package test.scala.uk.co.madsie.nanonet.util

import org.w3c.dom._

/*
 * Class for testing construct objects by elements 
 */

class ChildA extends ParentClass {

	def this(el : Element) = {this()}
	def this(el : Element,i : Integer) = {this()}
}

// vim: set ts=4 sw=4 et:
