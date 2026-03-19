package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.core.geo.GeoPoint

/**
 * Non-interactive map composable for displaying a route snapshot.
 *
 * Gestures are disabled by default. The camera auto-fits to show all provided
 * route and location points. Ideal for order summary or history screens.
 *
 * @since 0.0.1
 */
interface StaticMap {
    /**
     * Renders the static map composable.
     *
     * @param modifier Compose [Modifier] applied to the map container.
     * @param route Ordered list of coordinates defining the route polyline.
     * @param locations Coordinates for location markers (start, intermediate, finish).
     * @param startLabel Label text for the first location marker.
     * @param endLabel Label text for the last location marker.
     * @param onMapReady Callback invoked once the map tiles have loaded.
     * @since 0.0.1
     */
    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        route: List<GeoPoint>? = null,
        locations: List<GeoPoint>? = null,
        startLabel: String? = null,
        endLabel: String? = null,
        onMapReady: (() -> Unit)? = null
    )
}
