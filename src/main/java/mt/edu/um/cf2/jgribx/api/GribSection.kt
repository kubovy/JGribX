package mt.edu.um.cf2.jgribx.api

import mt.edu.um.cf2.jgribx.GribInputStream
import mt.edu.um.cf2.jgribx.GribOutputStream


/**
 * ### Common section part
 *
 *    | Octet | # | Value                           |
 *    |-------|---|---------------------------------|
 *    | 1-4   | 4 | Length of section in octets: nn |
 *    | 5     | 1 | Number of Section               |
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 */
interface GribSection {
	companion object {
		fun readFromStream(gribInputStream: GribInputStream, sectionNumber: Int): Int {
			/* [1-4] Section Length */
			val length = gribInputStream.readUINT(4)

			/* [5] Section Number */
			val number = gribInputStream.readUINT(1)
			if (number != sectionNumber) throw Exception("Incorrect section number ${number}")
			return length
		}
	}

	/** (`1-4`) Length of section in octets: `nn` */
	val length: Int

	/** (`5`) Number of Section */
	val number: Int

	/** Writes section to target [output stream][GribOutputStream] */
	fun writeTo(outputStream: GribOutputStream) {
		outputStream.writeUInt(length, bytes = 4) // [1-4] Section Length
		outputStream.writeUInt(number, bytes = 1) // [5] Section Number
	}
}
