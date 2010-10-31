package uk.co.madsie.nanonet.util

object TwoByteDecimal {

	def decode(bytes : Array[Byte]) : Double = {
		val iPart = (127 & bytes(0)).toDouble;
		val sign = if((128 & bytes(0)) > 0) { -1 }else{1}

		val bits = for(i <- 0 to 7) yield (2 << i,128 >> i)

		((iPart * sign) /: bits)((dPart,bit) => if((bit._2 & bytes(1)) > 0){ dPart + (sign * (1.0 / bit._1)) }else {dPart})
	}
}

// vim: set ts=4 sw=4 et:
