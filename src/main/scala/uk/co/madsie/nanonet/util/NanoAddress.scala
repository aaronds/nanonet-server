package uk.co.madsie.nanonet.util

import java.util.Arrays
import uk.co.madsie.nanonet.util._

case class NanoAddress(val address : Array[Byte]){

	def this(buffer : Array[Byte],offset : Int,length : Int) = {
		this(Arrays.copyOfRange(buffer,offset,length))
	}
	
	def equals(arg : NanoAddress) : Boolean = {
		if(address != null && arg.address != null){
			Arrays.equals(address,arg.address)
		}else{
			false
		}
	}

}


// vim: set ts=4 sw=4 et:
