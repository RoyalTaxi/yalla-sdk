package uz.yalla.maps.model

import androidx.compose.runtime.Immutable

/**
 * Configuration properties for the underlying map view.
 *
 * Controls feature toggles such as building extrusion, indoor maps,
 * traffic overlay, and zoom range. Applied to both Google Maps and
 * MapLibre where applicable.
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
