package uz.yalla.maps.model

/**
 * Represents a geographical location with latitude and longitude.
 *
 * Used by the compose-layer map primitives ([Marker][uz.yalla.maps.compose.Marker],
 * [Polyline][uz.yalla.maps.compose.Polyline], [Circle][uz.yalla.maps.compose.Circle]).
 * Converted to/from platform types in each actual implementation.
 *
 * @property latitude The latitude in degrees. Valid range is [-90, 90].
 * @property longitude The longitude in degrees. Valid range is [-180, 180].
 * @since 0.0.1
 */
data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    /**
     * Returns `true` if this coordinate is within valid geographic ranges.
     *
     * Latitude must be in [-90, 90] and longitude must be in [-180, 180].
     *
     * @return `true` when both latitude and longitude are within bounds.
     * @since 0.0.5
     */
    fun isValid(): Boolean =
        latitude in -90.0..90.0 && longitude in -180.0..180.0
}
