package uk.co.madsie.nanonet.server
import uk.co.madsie.nanonet.util._

class NanoPacket(val to : NanoAddress = null,val from : NanoAddress = null,val localPort : Byte = 0,val remotePort : Byte = 0,val dataLength : Int = 0,val data : Array[Byte] = null)

// vim: set ts=4 sw=4 et:
