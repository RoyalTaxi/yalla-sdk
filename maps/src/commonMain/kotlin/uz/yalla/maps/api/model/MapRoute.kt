package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

/**
 * A polyline route drawn on the map.
 *
 * @param id stable identity used to diff routes across [MapController.setRoutes] calls.
 * @param points the ordered vertices of the line.
 * @param colorArgb line color packed as ARGB.
 * @param widthDp line width in dp.
 * @param pattern line stroke pattern.
 * @param zIndex draw order; higher is on top.
 */
@Immutable
public data class MapRoute(
    val id: String,
    val points: List<GeoPoint>,
    val colorArgb: Int,
    val widthDp: Float = 4f,
    val pattern: RoutePattern = RoutePattern.SOLID,
    val zIndex: Float = 0f
)

/** Stroke pattern for a [MapRoute] line. */
public enum class RoutePattern { SOLID, DASHED, DOTTED }
