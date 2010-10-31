package uk.co.madsie.nanonet.server

import java.io.{InputStream,IOException};
import java.util.Arrays
import java.lang.reflect.{Constructor,InvocationTargetException}
import java.net.{InetAddress,ServerSocket,Socket}
import org.w3c.dom._
import javax.xml.parsers._
import javax.xml.xpath._
import scala.actors._,Actor._
import uk.co.madsie.nanonet.util._
import uk.co.madsie.nanonet.server.messages._
import concurrent.ops._

object NanoNet {
	def main(args : Array[String]): Unit = {
		if(args.length < 1){
			System.out.println("No Config Supplied");
		}

		val configPath = args(0);

		val domFactory = DocumentBuilderFactory.newInstance()
		domFactory.setNamespaceAware(true)
		val doc = domFactory.newDocumentBuilder.parse(configPath)
		val xpath = XPathFactory.newInstance.newXPath
		val nnEl = doc.getDocumentElement

		val nnName = nnEl.getAttribute("name");
		val nnAddressLength = nnEl.getAttribute("addressLength").toInt
		val nnDataLength = nnEl.getAttribute("dataLength").toInt
		val nnIdentiy = NetUtil.getAddressByDottedString(nnEl.getAttribute("identity"))
		val tcpPort = nnEl.getAttribute("port").toInt
		val bindAddress = InetAddress.getByName(nnEl.getAttribute("host"))
		val timeout = nnEl.getAttribute("timeout").toInt

		val server = new NanoServer(nnName,nnAddressLength,nnDataLength,nnIdentiy)

		val hostEls = NodeUtil.nodeSetToList(xpath.evaluate("/nanoNet/host",doc,XPathConstants.NODESET));

		val hosts = NodeUtil.constructObjectsByElement[NanoHost](hostEls,classOf[NanoDefaultHost],args = List[AnyRef](server))

		server.start

		server ! SetHosts(hosts)

		val serverSocket = new ServerSocket(tcpPort,0,bindAddress);

		acceptConnection(server,serverSocket,timeout);
		serverSocket.close
		server ! Exit
	}

	def acceptConnection(server : NanoServer,serverSocket : ServerSocket,timeout : Int = 100000) : Unit = {
		println("Waiting for connection");
		val socket = serverSocket.accept()

		if(socket == null){
			/*
			 * Something seriously wrong with server.
			 */
		}else{
			println("Accepting Connection")
			socket.setSoTimeout(timeout)
			spawn {
				val inputStream = socket.getInputStream
				val identityBytes = new Array[Byte](server.addressLength)
				val addressBytes = new Array[Byte](server.addressLength)

				if(!recvPart(inputStream,identityBytes)){
					throw new Exception("Idenitity")
				}

				if(!recvPart(inputStream,addressBytes)){
					throw new Exception("Address")
				}

				val identity = new NanoAddress(identityBytes)
				val address = new NanoAddress(addressBytes)

				if(identity equals server.identity){
					/*NB: Not safe to use inputStream now.*/
					println("Adding Socket")
					val intf = new NanoSocketInterface(address,server,socket)
					server ! Open(intf)
				}else{
					println("Failed")
					socket.close
				}
			}
			acceptConnection(server,serverSocket,timeout)
		}
	}


	def recvPart(inputStream : InputStream,buffer : Array[Byte],bufferLength : Int = 0) : Boolean = {
		val remainingBytes = buffer.length - bufferLength
		val bytesRead = inputStream.read(buffer,bufferLength,remainingBytes)

		bytesRead match {
			case 0 => {
				return false;
			}
			case `remainingBytes` => true
			case _ => recvPart(inputStream,buffer,bufferLength + bytesRead)
		}
	}
}

// vim: set ts=4 sw=4 et:
