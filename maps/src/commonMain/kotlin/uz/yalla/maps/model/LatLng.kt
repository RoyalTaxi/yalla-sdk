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
)
