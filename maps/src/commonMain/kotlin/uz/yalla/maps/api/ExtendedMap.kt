package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.MarkerState

/**
 * Full-featured map composable with route display, location markers, and custom content.
 *
 * Used for active ride screens, order tracking, and other scenarios that require
 * route polylines, multiple location pins, and programmatic camera control.
 * Supports an additional [MapScope]-scoped content lambda for custom overlays.
 *
 * @since 0.0.1
 */
interface ExtendedMap {
    /**
     * Renders the extended map composable.
     *
     * @param controller The [MapController] managing camera and marker state.
     * @param modifier Compose [Modifier] applied to the map container.
     * @param route Ordered list of coordinates defining the route polyline.
     * @param locations Coordinates for location markers (start, intermediate, finish).
     * @param initialPoint Optional starting coordinate; defaults to user location or fallback.
     * @param showLocationIndicator Whether to show the user-location dot overlay.
     * @param showMarkerLabels Whether to display text labels on start/end markers.
     * @param startMarkerLabel Label text for the first location marker.
     * @param endMarkerLabel Label text for the last location marker.
     * @param isInteractionEnabled Whether map gestures (scroll, zoom) are enabled.
     * @param useInternalCameraInitialization Whether the map manages its own initial camera positioning.
     * @param onMarkerChanged Callback invoked when the center marker position changes.
     * @param onMapReady Callback invoked once the map tiles have loaded.
     * @param content Additional composable content rendered within [MapScope].
     * @since 0.0.1
     */
    @Composable
    fun Content(
        controller: MapController,
        modifier: Modifier = Modifier,
        route: List<GeoPoint> = emptyList(),
        locations: List<GeoPoint> = emptyList(),
        initialPoint: GeoPoint? = null,
        showLocationIndicator: Boolean = true,
        showMarkerLabels: Boolean = true,
        startMarkerLabel: String? = null,
        endMarkerLabel: String? = null,
        isInteractionEnabled: Boolean = true,
        useInternalCameraInitialization: Boolean = true,
        onMarkerChanged: ((MarkerState) -> Unit)? = null,
        onMapReady: (() -> Unit)? = null,
        content: @Composable MapScope.() -> Unit = {}
    )
}
