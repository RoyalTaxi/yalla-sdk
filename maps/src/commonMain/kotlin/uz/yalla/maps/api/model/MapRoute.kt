package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

@Immutable
data class MapRoute(
    val id: String,
    val points: List<GeoPoint>,
    val colorArgb: Int,
    val widthDp: Float = 4f,
    val pattern: RoutePattern = RoutePattern.SOLID,
    val zIndex: Float = 0f
)

enum class RoutePattern { SOLID, DASHED, DOTTED }
