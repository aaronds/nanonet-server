package test.scala.uk.co.madsie.nanonet.util

import org.w3c.dom._
import javax.xml.parsers._
import javax.xml.xpath._

import uk.co.madsie.nanonet.util.NodeUtil

import test.scala.uk.co.madsie.nanonet.server._
import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server._
import java.util.Random

object Helper {
	
	val domFactory = DocumentBuilderFactory.newInstance()
	domFactory.setNamespaceAware(true)
	val docBuilder = domFactory.newDocumentBuilder
	val xpFactory = XPathFactory.newInstance
	
	def getElementList(file : String,xpath : String) : Iterable[Element] = {
		val xp = xpFactory.newXPath
		val dom = docBuilder.parse(file)

		NodeUtil.nodeSetToList(xp.evaluate(xpath,dom,XPathConstants.NODESET))
	}

	def getSvr = new NanoServer("Test",5,5,new NanoAddress(Array[Byte](1,2,3,4,5)))
	
	def getHost(name : String = "test",svr : NanoServer = null,address : Array[Byte] = null) : NanoTestHost = {
		new NanoTestHost(if(address == null){ getAddress() }else{new NanoAddress(address)},name,svr)
	}

	def getAddress(length : Int = 5) : NanoAddress = {
		val addrBytes = new Array[Byte](length)
		val rnd = new Random()
		rnd.nextBytes(addrBytes)
		new NanoAddress(addrBytes)
	}

	def printAddress(addr : NanoAddress){
		val stringEls = for(i <- 0 to addr.address.length - 1) yield addr.address(i).toString

		println(stringEls.mkString("."))
	}
}

// vim: set ts=4 sw=4 et:
