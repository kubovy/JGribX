package mt.edu.um.cf2.jgribx

import mt.edu.um.cf2.jgribx.grib2.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class GribFileTest {

	@Test
	fun testCutOutGrib2WithDRS3ToDRS0Conversion() {
		val url = GribTest::class.java.getResource("/nomads-1p00-f003-24h-s12.grb2")
		val bos = ByteArrayOutputStream()
		val gos = GribOutputStream(bos)

		val file1 = GribFile(url.openStream())
		file1.records
				.asSequence()
				.filterIsInstance<Grib2Record>()

				// Given
				.onEach { println("(A) ${it.dump()}") }
				.onEach { record ->
					record.productDefinition.dataSections.forEach { ds ->
						assertEquals(Grib2RecordDRS3::class, ds.drs::class)
						assertEquals(Grib2RecordDS3::class, ds::class)
					}
				}
				.onEach { assertEquals(65160, it.gridDefinition.coords.size) }
				.onEach { assertEquals(65160, it.values.size) }
				.onEach { assertEquals(-90.0, it.gridDefinition.yCoords.minOrNull()) }
				.onEach { assertEquals(90.0, it.gridDefinition.yCoords.maxOrNull()) }
				.onEach { assertEquals(-180.0, it.gridDefinition.xCoords.minOrNull()) }
				.onEach { assertEquals(179.0, it.gridDefinition.xCoords.maxOrNull()) }

				// When
				.onEach { it.convertDataRepresentationTo(Grib2RecordDRS0::class) }

				// Then
				.onEach { record ->
					record.productDefinition.dataSections.forEach { ds ->
						assertEquals(Grib2RecordDRS0::class, ds.drs::class)
						assertEquals(Grib2RecordDS0::class, ds::class)
					}
				}
				.onEach { assertEquals(65160, it.gridDefinition.coords.size) }
				.onEach { assertEquals(65160, it.values.size) }
				.onEach { assertEquals(-90.0, it.gridDefinition.yCoords.minOrNull()) }
				.onEach { assertEquals(90.0, it.gridDefinition.yCoords.maxOrNull()) }
				.onEach { assertEquals(-180.0, it.gridDefinition.xCoords.minOrNull()) }
				.onEach { assertEquals(179.0, it.gridDefinition.xCoords.maxOrNull()) }
				.forEach { println("(B) ${it.dump()}") }
		file1.writeTo(gos)

		// Given
		val file2 = GribFile(GribInputStream(ByteArrayInputStream(bos.toByteArray())))
		file2.records
				.asSequence()
				.filterIsInstance<Grib2Record>()
				.onEach { assertEquals(65160, it.gridDefinition.coords.size) }
				.onEach { assertEquals(65160, it.values.size) }
				.onEach { assertEquals(-90.0, it.gridDefinition.yCoords.minOrNull()) }
				.onEach { assertEquals(90.0, it.gridDefinition.yCoords.maxOrNull()) }
				.onEach { assertEquals(-180.0, it.gridDefinition.xCoords.minOrNull()) }
				.onEach { assertEquals(179.0, it.gridDefinition.xCoords.maxOrNull()) }
				.forEach { println("(C) ${it.dump()}") }

		// When
		file2.cutOut(50.0, 20.0, 40.0, 10.0)

		// Then
		file2.records
				.asSequence()
				.filterIsInstance<Grib2Record>()
				.onEach { assertEquals(144, it.gridDefinition.coords.size) }
				.onEach { assertEquals(144, it.values.size) }
				.onEach { assertEquals(40.0, it.gridDefinition.yCoords.minOrNull()) }
				.onEach { assertEquals(51.0, it.gridDefinition.yCoords.maxOrNull()) }
				.onEach { assertEquals(9.0, it.gridDefinition.xCoords.minOrNull()) }
				.onEach { assertEquals(20.0, it.gridDefinition.xCoords.maxOrNull()) }
				.forEach { println("(D) ${it.dump()}") }
		bos.reset()
		file2.writeTo(gos)

		val file3 = GribFile(GribInputStream(ByteArrayInputStream(bos.toByteArray())))
		file3.records
				.asSequence()
				.filterIsInstance<Grib2Record>()
				.onEach { assertEquals(144, it.gridDefinition.coords.size) }
				.onEach { assertEquals(144, it.values.size) }
				.onEach { assertEquals(40.0, it.gridDefinition.yCoords.minOrNull()) }
				.onEach { assertEquals(51.0, it.gridDefinition.yCoords.maxOrNull()) }
				.onEach { assertEquals(9.0, it.gridDefinition.xCoords.minOrNull()) }
				.onEach { assertEquals(20.0, it.gridDefinition.xCoords.maxOrNull()) }
				.forEach { println("(E) ${it.dump()}") }
	}

	private fun Grib2Record.dump() = "${parameter}: coords=${gridDefinition.coords.size}, data=${values.size}"
}
