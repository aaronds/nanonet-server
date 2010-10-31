package uk.co.madsie.nanonet.server
import java.net.Socket;
import java.util.Arrays;
import scala.actors._,Actor._
import concurrent.ops._
import uk.co.madsie.nanonet.server.messages._
import uk.co.madsie.nanonet.util._

/**
 * A tcp socket interface for nano net.
 */

class NanoSocketInterface(address : NanoAddress,server : NanoServer,val socket : Socket) extends NanoInterface(address,server) {
	
	val inputStream = socket.getInputStream();
	val outputStream = socket.getOutputStream();

	val packetLength = (2 * server.addressLength) + server.dataLength + 3;

	/**
	 * Receive a packet from the socket.
	 *
	 * @param	timeout	Time in ms to wait before closing the socket.
	 */

	override def recv(timeout: Long) : NanoPacket = {
		val buffer = new Array[Byte](packetLength)
		recvPart(buffer,0,timeout)
	}

	/**
	 * handle partial packets
	 *
	 * @param	buffer	The array to read into.
	 * @param	bufferLength	The number of bytes already read.
	 * @param	timeout
	 */

	def recvPart(buffer : Array[Byte],bufferLength : Int,timeout : Long) : NanoPacket = {
		val remainingBytes = packetLength - bufferLength
		val bytesRead = inputStream.read(buffer,bufferLength,remainingBytes)

		bytesRead match {
			case 0 => {
				return null;
			}
			case `remainingBytes` => buildPacket(buffer)
			case _ => recvPart(buffer,bufferLength + bytesRead,timeout)
		}
	}

	/**
	 * Construct a NanoPacket from an array of bytes.
	 */

	def buildPacket(buffer : Array[Byte]) : NanoPacket = {
		val to = new NanoAddress(buffer,0,server.addressLength)
		val localPort = buffer(server.addressLength)
		val remotePort = buffer(server.addressLength + 1)
		val dataLength = 0xff & buffer(server.addressLength + 2)
		val from = new NanoAddress(buffer,server.addressLength + 3,(server.addressLength * 2) + 3)
		val data = if(dataLength > 0){ Arrays.copyOfRange(buffer,(server.addressLength * 2) + 3,(server.addressLength * 2) + 3 + dataLength)}else{new Array[Byte](0)}

		new NanoPacket(to,from,localPort,remotePort,dataLength,data)
	}

	/**
	 * Send a packet out the interface
	 */

	override def send(p : NanoPacket) : Unit = {
		/* Send via outputStream */
		outputStream.write(p.to.address)
		outputStream.write(p.remotePort)
		outputStream.write(p.localPort)
		outputStream.write((0xff & p.dataLength).asInstanceOf[Byte])
		outputStream.write(p.from.address)
		/* Pad to compleate packet */
		val response = Arrays.copyOf(p.data,server.dataLength)
		outputStream.write(response);
	}

	/**
	 * Close this interface
	 */

	override def end() : Unit = {
		socket.close
	}

	/**
	 * Spawn a thread to read this socket.
	 *
	 * TODO: replace with better IO model.
	 */

	spawn {
		def getAndSend : Unit = {
			val packet = recv(0)
			if(packet != null){
				this ! Receive(packet)
				getAndSend
			}else{
				this ! Exit
			}
		}
		getAndSend
	}
}
// vim: set ts=4 sw=4 et:
