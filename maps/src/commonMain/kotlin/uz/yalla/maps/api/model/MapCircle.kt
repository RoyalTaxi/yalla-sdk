package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

/**
 * A geographic circle drawn on the map (e.g. a search/pickup radius).
 *
 * @param id stable identity used to diff circles across [MapController.setCircles] calls.
 * @param center the circle center.
 * @param radiusMeters radius in meters.
 * @param fillColorArgb fill color packed as ARGB.
 * @param strokeColorArgb stroke color packed as ARGB; 0 for no stroke.
 * @param strokeWidthDp stroke width in dp.
 * @param zIndex draw order; higher is on top.
 */
@Immutable
public data class MapCircle(
    val id: String,
    val center: GeoPoint,
    val radiusMeters: Double,
    val fillColorArgb: Int,
    val strokeColorArgb: Int = 0,
    val strokeWidthDp: Float = 1f,
    val zIndex: Float = 0f
)
