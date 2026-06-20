package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

/**
 * A point marker on the map.
 *
 * @param id stable identity used to diff markers across [MapController.setMarkers] calls.
 * @param point geographic location of the marker.
 * @param icon how to render the marker; null uses the platform default.
 * @param rotation visual heading in degrees. For animated driver markers this is the raw server
 *   heading fed to `DriverMotionModel.push` (0f is treated as "absent" by the motion model).
 * @param routeHeading optional route-segment heading hint (null when off-route).
 * @param anchor the point of the icon pinned to [point].
 * @param flat true to keep the marker flat against the map (rotating with it) rather than billboarded.
 * @param zIndex draw order; higher is on top.
 * @param contentDescription accessibility label.
 */
@Immutable
public data class MapMarker(
    val id: String,
    val point: GeoPoint,
    val icon: MapMarkerIcon? = null,
    val rotation: Float = 0f,
    val routeHeading: Float? = null,
    val anchor: Anchor = Anchor.BOTTOM,
    val flat: Boolean = false,
    val zIndex: Float = 0f,
    val contentDescription: String? = null
)
