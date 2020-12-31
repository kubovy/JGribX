package mt.edu.um.cf2.jgribx

/**
 * A class that represents an exception thrown when a GRIB record is skipped due to filtering.
 *
 * @author  Jan Kubovy
 */
class SkippedException(msg: String) : Exception(msg) {
	companion object {
		private const val serialVersionUID = 1L
	}
}