package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

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
    val contentDescription: String? = null,
    /**
     * Id of the [MapRoute] this marker follows along, or `null` for a free-roaming marker.
     *
     * A flat driver marker sets this to declare "I follow the route with this id"; the renderer
     * then drives the marker's position and heading from that route's arc-length progress so the
     * car stays glued to the drawn polyline through corners instead of cutting straight across.
     */
    val followsRouteId: String? = null
)
