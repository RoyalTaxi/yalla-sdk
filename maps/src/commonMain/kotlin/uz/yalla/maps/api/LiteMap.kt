package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.MarkerState

/**
 * Lightweight map composable for simple pin-on-map use cases.
 *
 * Displays a single-marker map with optional user-location tracking.
 * Suitable for address selection or order pickup point screens where
 * full route/layer support is unnecessary.
 *
 * @since 0.0.1
 */
interface LiteMap {
    /**
     * Renders the lite map composable.
     *
     * @param controller The [MapController] managing camera and marker state.
     * @param modifier Compose [Modifier] applied to the map container.
     * @param initialPoint Optional starting coordinate; defaults to user location or fallback.
     * @param showLocationIndicator Whether to show the user-location dot overlay.
     * @param bindLocationTracker Whether to start location tracking automatically.
     * @param onMarkerChanged Callback invoked when the center marker position changes.
     * @param onMapReady Callback invoked once the map tiles have loaded.
     * @since 0.0.1
     */
    @Composable
    fun Content(
        controller: MapController,
        modifier: Modifier = Modifier,
        initialPoint: GeoPoint? = null,
        showLocationIndicator: Boolean = true,
        bindLocationTracker: Boolean = true,
        onMarkerChanged: ((MarkerState) -> Unit)? = null,
        onMapReady: (() -> Unit)? = null
    )
}
