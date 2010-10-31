package uk.co.madsie.nanonet.util

object NetUtil {
	def getAddressByDottedString(dottedString : String) : NanoAddress = {
		new NanoAddress(dottedString.split('.').map((dec) => {
			val decInt = dec.toInt & 255
			(if(decInt > 128){ (-1 * ((decInt - 128)))}else{decInt}).asInstanceOf[Byte]
		}).toArray)
	}
}

// vim: set ts=4 sw=4 et:
