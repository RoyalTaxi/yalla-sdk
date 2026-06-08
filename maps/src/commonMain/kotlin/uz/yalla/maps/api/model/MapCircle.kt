package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

@Immutable
data class MapCircle(
    val id: String,
    val center: GeoPoint,
    val radiusMeters: Double,
    val fillColorArgb: Int,
    val strokeColorArgb: Int = 0,
    val strokeWidthDp: Float = 1f,
    val zIndex: Float = 0f
)
