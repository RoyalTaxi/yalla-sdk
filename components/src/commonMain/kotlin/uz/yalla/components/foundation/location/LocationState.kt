package uz.yalla.components.foundation.location

import uz.yalla.core.geo.GeoPoint

/**
 * Extended location data with additional metadata.
 *
 * @property latitude Latitude coordinate
 * @property longitude Longitude coordinate
 * @property accuracy Location accuracy in meters
 * @property altitude Altitude in meters (if available)
 * @property speed Speed in m/s (if available)
 * @property bearing Bearing in degrees (if available)
 * @property timestamp Timestamp when location was captured
 */
data class ExtendedLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val timestamp: Long = 0L,
) {
    /** Converts to [GeoPoint]. */
    fun toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
}

/**
 * Location permission state.
 */
enum class LocationPermissionState {
    /** Permission not yet requested. */
    NOT_DETERMINED,

    /** Permission granted. */
    GRANTED,

    /** Permission denied. */
    DENIED,

    /** Permission denied permanently (user selected "Don't ask again"). */
    DENIED_PERMANENTLY,
}
