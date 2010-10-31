package uk.co.madsie.nanonet.server
import org.w3c.dom._

import net.iharder.Base64;
import java.io.{BufferedReader,InputStreamReader,OutputStreamWriter};
import java.net.{URL,URLConnection}

/**
 * The default port handler
 * takes a url as an attribute in the xml config and sends the base64 encoded request 
 * data to that url, with the from and to address and ports sent as get parameters.
 *
 * Any response is then base64 decoded and returned to the nano net host.
 */

class NanoRestPort(number : Byte,name : String,val url : String) extends NanoPort(number,name) {

	def this(el : Element) = {
		this(el.getAttribute("number").toByte,el.getAttribute("name"),el.getAttribute("url"))
	}

	override def service(server : NanoServer,host : NanoHost,packet : NanoPacket) : Unit = {
		try{
			val lport = packet.localPort.toString
			val rport = packet.remotePort.toString
			val fromAddr = Base64.encodeBytes(packet.from.address,Base64.URL_SAFE)
			val toAddr = Base64.encodeBytes(packet.to.address,Base64.URL_SAFE)
			val data = Base64.encodeBytes(packet.data)
			val fullUrl = url + (if(url.indexOf('?') > 0){ "&fromAddr=" + fromAddr }else{"?fromAddr=" + fromAddr}) + "&lport=" + lport + "&rport=" + rport;
			val reqUrl = new URL(fullUrl)
			val con = reqUrl.openConnection();
			
			con.setDoOutput(true)
			con.setDoInput(true)

			val wr = new OutputStreamWriter(con.getOutputStream)
			wr.write(data)
			wr.flush()

			val rd = new BufferedReader(new InputStreamReader(con.getInputStream))
			val line = rd.readLine
			wr.close
			rd.close

			val response = if(line == null){ Array[Byte]()}else{ Base64.decode(line) }

			if(response.length > 0){
				server.send(packet.from,host,packet.remotePort,packet.localPort,response)
			}

		}
	}
}

// vim: set ts=4 sw=4 et:
