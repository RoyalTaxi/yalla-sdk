package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.core.geo.GeoPoint

/**
 * Non-interactive map composable for displaying a route snapshot.
 *
 * Gestures are disabled by default. The camera auto-fits to show all provided
 * route and location points. Ideal for order summary or history screens.
 */
interface StaticMap {
    /**
     * Renders the static map composable.
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
