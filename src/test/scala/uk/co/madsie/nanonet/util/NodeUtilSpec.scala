package test.scala.uk.co.madsie.nanonet.util

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}
import uk.co.madsie.nanonet.util.NodeUtil

import org.w3c.dom._
import javax.xml.parsers._
import javax.xml.xpath._

class NodeUtilTest extends JUnit4(NodeUtilSpec)

object NodeUtilTestRunner extends ConsoleRunner(NodeUtilSpec)

object NodeUtilSpec extends Specification {

	val domFactory = DocumentBuilderFactory.newInstance()
	domFactory.setNamespaceAware(true)
	val docBuilder = domFactory.newDocumentBuilder
	val xpFactory = XPathFactory.newInstance


	def loadXML(file : String) = docBuilder.parse(file)
	def getXPath = xpFactory.newXPath

	"nodeSetToList" should {

		"Return an element list from a node set containing nodes" in {
			val dom = loadXML("target/test-classes/node-util-spec_node-set-to-list.xml")
			val xp = getXPath
			val elements = NodeUtil.nodeSetToList(xp.evaluate("/root/Element",dom,XPathConstants.NODESET));
			elements must notBeEmpty
			elements.length must beEqualTo(3)
		}
	}

	def getElementList = {
		val dom = loadXML("target/test-classes/node-util-spec_construct-objects-by-elements.xml")
		val xp = getXPath

		NodeUtil.nodeSetToList(xp.evaluate("/root/Element",dom,XPathConstants.NODESET));
	}


	"constructObjectsByElement" should {
		"produce a list of T objects" in {

			val elements = getElementList 
			val objects = NodeUtil.constructObjectsByElement[ParentClass](elements,classOf[ChildA])

			objects must notBeEmpty
		}

		"choose the corect default class where one is not specified" in {
			val objects = NodeUtil.constructObjectsByElement[ParentClass](getElementList,classOf[ChildA])

			/* NB: Due to folding head is the last element in the file.*/

			objects.head must haveClass[ChildA]
		}

		"construct the correct class when the class attribute is set" in {
			val objects = NodeUtil.constructObjectsByElement[ParentClass](getElementList,classOf[ChildA])
			objects.tail.tail.head must haveClass[ChildB]
		}

		"support passing extra parameters to the constructor with the args parameter" in {
			val dom = loadXML("target/test-classes/node-util-spec_extra-param-args.xml")
			val xp = getXPath

			val els = NodeUtil.nodeSetToList(xp.evaluate("/root/Element",dom,XPathConstants.NODESET));
			val objects = NodeUtil.constructObjectsByElement[ParentClass](els,classOf[ChildA],args = List(100.asInstanceOf[AnyRef]))

			val obj = objects.head.asInstanceOf[ChildC]

			obj.i must beEqualTo(100)
		}
	}
}

// vim: set ts=4 sw=4 et:
