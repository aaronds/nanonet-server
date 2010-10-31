package uk.co.madsie.nanonet.util

object Endian {

	def reverse(array : Array[Byte]) : Array[Byte] = {
		val rv  = Array.ofDim[Byte](array.length)

		for(i <- (0 to array.length - 1)){
			rv((array.length - 1) - i) = array(i)
		}

		rv
	}
}
// vim: set ts=4 sw=4 et:
