package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

@Immutable
data class MapMarker(
    val id: String,
    val point: GeoPoint,
    val icon: MapMarkerIcon? = null,
    val rotation: Float = 0f,
    val anchor: Anchor = Anchor.BOTTOM,
    val flat: Boolean = false,
    val zIndex: Float = 0f,
    val contentDescription: String? = null
)
