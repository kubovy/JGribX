/*
 * ============================================================================
 * JGribX
 * ============================================================================
 * Written by Andrew Spiteri <andrew.spiteri@um.edu.mt>
 * Adapted from JGRIB: http://jgrib.sourceforge.net/
 * 
 * Licensed under MIT: https://github.com/spidru/JGribX/blob/master/LICENSE
 * ============================================================================
 */
package mt.edu.um.cf2.jgribx.grib2

import mt.edu.um.cf2.jgribx.GribInputStream
import mt.edu.um.cf2.jgribx.Logger
import mt.edu.um.cf2.jgribx.NotSupportedException

/**
 * [Section 5: Data Representation Section](https://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_doc/grib2_sect5.shtml)
 *
 * @param length [ 1- 4] Length of the section in octets (nn)
 * @param nDataPoints [ 6- 9] Number of data points where one or more values are specified in Section 7 when a bit
 *                            map is present, total number of data points when a bit map is absent.
 * @param templateNumber [10-11] Data representation template number [Table 5.0](https://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_doc/grib2_table5-0.shtml)
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 */
abstract class Grib2RecordDRS(
		internal val length: Int,
		internal val nDataPoints: Int,
		internal val templateNumber: Int) {

	companion object {
		fun readFromStream(gribInputStream: GribInputStream): Grib2RecordDRS? {
			val length = gribInputStream.readUINT(4)  // [1-4] Length of the section in octets (nn)
			val section = gribInputStream.readUINT(1) // [5] Number of the section (5)
			if (section != 5) {
				Logger.error("DRS contains an incorrect section number ${section}!")
				return null
			}
			val nDataPoints = gribInputStream.readUINT(4)
			val templateNumber = gribInputStream.readUINT(2)
			return when (templateNumber) {
				0 -> Grib2RecordDRS0.readFromStream(gribInputStream, length, nDataPoints, templateNumber)
				2 -> Grib2RecordDRS2.readFromStream(gribInputStream, length, nDataPoints, templateNumber)
				3 -> Grib2RecordDRS3.readFromStream(gribInputStream, length, nDataPoints, templateNumber)
				else -> throw NotSupportedException("Data Representation type ${templateNumber} not supported")
			}
		}
	}
}
