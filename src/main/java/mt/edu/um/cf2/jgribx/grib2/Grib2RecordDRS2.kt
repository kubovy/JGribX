package mt.edu.um.cf2.jgribx.grib2

import mt.edu.um.cf2.jgribx.GribInputStream

/**
 * ### [5.2 Grid point data - complex packing](https://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_doc/grib2_temp5-2.shtml)
 *
 *    | Octet         |  #  | Value                                                                   |
 *    |---------------|-----|-------------------------------------------------------------------------|
 *    | `1 - 4`       | `4` | Length of section in octets: `nn`                                       |
 *    | `5`           | `1` | Number of Section: `5`                                                  |
 *    | `6 - 9`       | `4` | Number of data points where one or more values are specified in         |
 *    |               |     | Section 7 when a bit map is present, total number of data points when a |
 *    |               |     | bit map is absent                                                       |
 *    | `10 - 11`     | `2` | Data Representation Template Number (see code Table 5.0)                |
 *    | `12 - 15`     | `4` | Reference value (R) (IEEE 32-bit floating-point value)                  |
 *    | `16 - 17`     | `2` | Binary Scale Factor (E)                                                 |
 *    | `18 - 19`     | `2` | Decimal Scale Factor (D)                                                |
 *    | `20`          | `1` | Number of bits used for each packed value for simple packing, or for    |
 *    |               |     | each group reference value for complex packing or spatial differencing  |
 *    | `21`          | `1` | Type of original field values (see Code Table 5.1)                      |
 *    | `22`          | `1` | Group splitting method used (see Code Table 5.4)                        |
 *    | `23`          | `1` | Missing value management used (see Code Table 5.5)                      |
 *    | `24-27`       | `4` | Primary missing value substitute                                        |
 *    | `28-31`       | `4` | Secondary missing value substitute                                      |
 *    | `32-35`       | `4` | NG ― number of groups of data values into which field is split          |
 *    | `36`          | `1` | Reference for group widths (see Note 12)                                |
 *    | `37`          | `1` | Number of bits used for the group widths (after the reference value in  |
 *    |               |     | octet 36 has been removed)                                              |
 *    | `38-41`       | `4` | Reference for group lengths (see Note 13)                               |
 *    | `42`          | `1` | Length increment for the group lengths (see Note 14)                    |
 *    | `43-46`       | `4` | True length of last group                                               |
 *    | `47`          | `1` | Number of bits used for the scaled group lengths (after subtraction of  |
 *    |               |     | the reference value given in octets 38-41 and division by the length    |
 *    |               |     | increment given in octet 42)                                            |
 *
 *     Y = ( R + (X1 + X2) * 2^E ) / 10^D
 *
 * @param length                  [ 1- 4] Length of the section in octets (nn)
 * @param nDataPoints             [ 6- 9] Number of data points where one or more values are specified in Section 7
 *                                        when a bit map is present, total number of data points when a bit map is
 *                                        absent.
 * @param templateNumber          [10-11] Data representation template number
 *                                        [Table 5.0](https://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_doc/grib2_table5-0.shtml)
 * @param refValue                [12-15] Reference value (R) (IEEE 32-bit floating-point value)
 * @param binaryScaleFactor       [16-17] Binary scale factor (E)
 * @param decimalScaleFactor      [18-19] Decimal scale factor (D)
 * @param nBits                   [20]    Number of bits used for each packed value for simple packing, or for each
 *                                        group reference value for complex packing or spatial differencing
 * @param type                    [21]    Type of original field values
 *                                        [Table 5.1](https://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_doc/grib2_table5-1.shtml)
 * @param splitMethod             [22]    Group splitting method used
 *                                        [Table 5.4](https://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_doc/grib2_table5-4.shtml)
 * @param missingValueManagement  [23]    Missing value management used
 *                                        [Table 5.5](https://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_doc/grib2_table5-5.shtml)
 * @param missingValue            [24-27] Primary missing value substitute
 *                                [28-31] Secondary missing value substitute
 * @param nGroups                 [32-35] NG ― number of groups of data values into which field is split
 * @param refGroupWidths          [36]    Reference for group widths
 * @param groupWidthBits          [37]    Number of bits used for the group widths (after the reference value in
 *                                        octet 36 has been removed)
 * @param refGroupLengths         [38-41] Reference for group lengths
 * @param groupLengthIncrement    [42]    Length increment for the group lengths
 * @param lastGroupLength         [43-46] True length of last group
 * @param nBitsScaledGroupLengths [47]    Number of bits used for the scaled group lengths (after subtraction of the
 *                                        reference value given in octets 38-41 and division by the length increment
 *                                        given in octet 42)
 * @author Jan Kubovy [jan@kubovy.eu]
 */
open class Grib2RecordDRS2(length: Int,
						   nDataPoints: Int,
						   templateNumber: Int,
						   refValue: Float,
						   binaryScaleFactor: Int,
						   decimalScaleFactor: Int,
						   nBits: Int,
						   type: Int,

						   internal val splitMethod: Int,
						   internal val missingValueManagement: Int,
						   internal val missingValue: FloatArray,
						   internal val nGroups: Int,
						   internal val refGroupWidths: Int,
						   internal val groupWidthBits: Int,
						   internal val refGroupLengths: Int,
						   internal val groupLengthIncrement: Int,
						   internal val lastGroupLength: Int,
						   internal val nBitsScaledGroupLengths: Int) :
		Grib2RecordDRS0(length, nDataPoints, templateNumber, refValue, binaryScaleFactor, decimalScaleFactor, nBits,
				type) {

	companion object {
		fun readFromStream(gribInputStream: GribInputStream,
						   length: Int,
						   nDataPoints: Int,
						   templateNumber: Int): Grib2RecordDRS2 {
			val drs0 = Grib2RecordDRS0.readFromStream(gribInputStream, length, nDataPoints, templateNumber)
			val splitMethod = gribInputStream.readUINT(1)
			val missingValueManagement = gribInputStream.readUINT(1)
			val missing1 = gribInputStream.readUINT(4)
			val missing2 = gribInputStream.readUINT(4)
			val missingValue = when (missingValueManagement) {
				0 -> floatArrayOf(Float.NaN, Float.NaN)
				1 -> floatArrayOf(missing1.toFloat(), Float.NaN)
				2 -> floatArrayOf(missing1.toFloat(), missing2.toFloat()) // FIXME not sure about this
				else -> floatArrayOf(Float.NaN, Float.NaN)
			}
			val nGroups = gribInputStream.readUINT(4)
			val refGroupWidths = gribInputStream.readUINT(1)
			val groupWidthBits = gribInputStream.readUINT(1)
			val refGroupLengths = gribInputStream.readUINT(4)
			val groupLengthIncrement = gribInputStream.readUINT(1)
			val lastGroupLength = gribInputStream.readUINT(4)
			val nBitsScaledGroupLengths = gribInputStream.readUINT(1)

			return Grib2RecordDRS2(drs0, splitMethod, missingValueManagement, missingValue, nGroups, refGroupWidths,
					groupWidthBits, refGroupLengths, groupLengthIncrement, lastGroupLength, nBitsScaledGroupLengths)
		}
	}

	private constructor(drs0: Grib2RecordDRS0, splitMethod: Int, missingValueManagement: Int, missingValue: FloatArray,
						nGroups: Int, refGroupWidths: Int, groupWidthBits: Int, refGroupLengths: Int, groupLengthIncrement: Int,
						lastGroupLength: Int, nBitsScaledGroupLengths: Int) : this(drs0.length, drs0.nDataPoints,
			drs0.templateNumber, drs0.refValue, drs0.binaryScaleFactor, drs0.decimalScaleFactor, drs0.nBits, drs0.type,
			splitMethod, missingValueManagement, missingValue, nGroups, refGroupWidths, groupWidthBits, refGroupLengths,
			groupLengthIncrement, lastGroupLength, nBitsScaledGroupLengths)
}
