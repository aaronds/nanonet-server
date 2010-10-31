package test.scala.uk.co.madsie.nanonet.server

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4}
import uk.co.madsie.nanonet.server._
import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server.messages._
import scala.actors._,Actor._
import test.scala.uk.co.madsie.nanonet.util.Helper._
import java.util.Random
import org.scalacheck.Prop


class NanoServerTest extends JUnit4(NanoServerSpec)

object NanoServerTestRunner extends ConsoleRunner(NanoServerSpec)

object NanoServerSpec extends Specification with ScalaCheck {

	implicit def bool2int(b:Boolean) : Int = { if(b) { 1 }else{0}}
	
	"constructor" should {
		"work with explicit paramters" in {
			val svr = new NanoServer("Test",5,5,new NanoAddress(Array[Byte](1,2,3,4,5)))
			svr must haveClass[NanoServer]
		}
	}

	def getSvr = new NanoServer("Test",5,5,new NanoAddress(Array[Byte](1,2,3,4,5)))

	"as an actor it " should {
		shareVariables()
		setSequential()
		val svr = getSvr

		def getStatus = (svr !? Status()).asInstanceOf[NanoServerConfig]

		"start ok" in {

			svr.start

			val status = svr !?(1000,new Status())

			status must beSomething
		}

		"status must return a NanoServerConfig object" in {

			val config = (svr !? Status()) must haveClass[NanoServerConfig]
		}

		val host = getHost(address = Array[Byte](10,0,0,0,1))

		"add a host" in {
			svr ! AddHost(host)

			getStatus.hosts must contain(host)
		}

		"call begin on added host" in {
			host.beginCalled must beTrue
		}

		
		"remove host" in {
			svr ! RemHost(host)

			getStatus.hosts must notContain(host)

		}

		"call end on removed host" in {
			host.endCalled must beTrue
		}
		
		val hosts = List(getHost(),getHost(),getHost())

		"set list of hosts" in {

			svr ! SetHosts(hosts)

			getStatus.hosts must haveTheSameElementsAs(hosts)
		}

		"call begin on list of hosts" in {
			val countTrue = ( 0 /: hosts) (_ + _.beginCalled)
			countTrue must beEqual(3)
		}

		"call end on removed elements" in {
			svr ! SetHosts(List[NanoHost]())

			getStatus

			( 0 /: hosts) (_ + _.endCalled) must beEqual(3)
		}
		
		val hAddr = Array[Byte](10,0,0,0,10)
		val addrHost = getHost(address = hAddr)
		val addrHost2 = getHost(address = hAddr)
		val addrHost3 = getHost(address = hAddr)

		"remove host by address" in {
			svr ! AddHost(addrHost)

			svr ! RemHostByAddress(addrHost2)

			getStatus.hosts must beEmpty
		}

		"call end on removed host" in {
			addrHost.endCalled must beTrue
		}

		"replace host by address" in {

			svr ! AddHost(addrHost2)

			svr ! ReplaceHostByAddress(addrHost3)

			getStatus.hosts must notContain(addrHost2) and contain(addrHost3)
		}

		"replace host must call end on old host" in {
			addrHost2.endCalled must beTrue
		}

		"replace host must call begin on new host" in {
			addrHost3.beginCalled must beTrue
		}

		val intfAddr = new NanoAddress(Array[Byte](10,0,0,0,20))

		val intf = new NanoTestInterface(intfAddr,svr)

		intf.start

		"open a new interface" in {
			svr ! Open(intf)
			getStatus.interfaces must contain(intf)
		}

		val intfTwo = new NanoTestInterface(intfAddr,svr)

		"replace an interface" in {
			svr ! Open(intfTwo)
			getStatus.interfaces must notContain(intf)
		}

		"close an old interface" in {
			svr ! Close(intfTwo)
			getStatus.interfaces must notContain(intfTwo)
		}

		"route packets to hosts" in {
			val p = new NanoPacket(to = new NanoAddress(hAddr),from = intfAddr,localPort = 1,remotePort = 1)
			svr ! Receive(p)

			getStatus

			addrHost3.serviceCalled must beTrue
		}

		"route packets to interface" in {
			svr ! Open(intf)
			val p = new NanoPacket(to = intfAddr,from = new NanoAddress(hAddr),localPort = 1,remotePort = 1)

			svr ! Send(p)

			intf.extractSend must beSome(p)
		}

		"the send method should route appropriately" in {
			svr.send(intfAddr,addrHost3,2,4,Array[Byte]())
			val p = intf.extractSend.get
			p.localPort must beEqual(4) 
		}
	}
}
// vim: set ts=4 sw=4 et:
