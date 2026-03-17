package uz.yalla.foundation.location

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
 * @since 0.0.1
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
    /**
     * Converts to [GeoPoint].
     *
     * @since 0.0.1
     */
    fun toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
}

/**
 * Location permission state.
 *
 * @since 0.0.1
 */
enum class LocationPermissionState {
    /**
     * Permission not yet requested.
     *
     * @since 0.0.1
     */
    NOT_DETERMINED,

    /**
     * Permission granted.
     *
     * @since 0.0.1
     */
    GRANTED,

    /**
     * Permission denied.
     *
     * @since 0.0.1
     */
    DENIED,

    /**
     * Permission denied permanently (user selected "Don't ask again").
     *
     * @since 0.0.1
     */
    DENIED_PERMANENTLY,
}
