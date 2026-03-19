package uz.yalla.maps.model

import androidx.compose.runtime.Immutable

/**
 * Configuration properties for the underlying map view.
 *
 * Controls feature toggles such as building extrusion, indoor maps,
 * traffic overlay, and zoom range. Applied to both Google Maps and
 * MapLibre where applicable.
 *
 * @property isBuildingEnabled Whether 3D building extrusion is enabled.
 * @property isIndoorEnabled Whether indoor floor plans are enabled.
 * @property isMyLocationEnabled Whether the native my-location layer is enabled.
 * @property isTrafficEnabled Whether real-time traffic overlay is enabled.
 * @property mapType The base tile type (normal, satellite, hybrid, terrain).
 * @property minZoomPreference Minimum zoom level the user can reach.
 * @property maxZoomPreference Maximum zoom level the user can reach.
 * @since 0.0.1
 */
@Immutable
data class MapProperties(
    val isBuildingEnabled: Boolean = false,
    val isIndoorEnabled: Boolean = false,
    val isMyLocationEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false,
    val mapType: MapType = MapType.NORMAL,
    val minZoomPreference: Float = 3.0f,
    val maxZoomPreference: Float = 21.0f,
)
