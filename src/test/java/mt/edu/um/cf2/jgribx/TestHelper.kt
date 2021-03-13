package mt.edu.um.cf2.jgribx

import org.junit.Assert.assertTrue
import kotlin.math.pow

fun Int.byteSpace() = (this * 8).bitSpace()

fun Int.bitSpace() = 2.0.pow(this).toInt()

fun assertContentEquals(message: String, expected: ByteArray, actual: ByteArray) = assertTrue(
		"${message}\n          %s\nExpected: %s\nActual  : %s".format(
				(1..expected.size).joinToString(" ") { "%02d".format(it) },
				expected.joinToString(" ") { "%02X".format(it) },
				actual.joinToString(" ") { "%02X".format(it) }),
		expected.contentEquals(actual))
