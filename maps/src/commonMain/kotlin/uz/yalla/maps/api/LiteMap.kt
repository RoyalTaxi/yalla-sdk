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
 */
interface LiteMap {
    /**
     * Renders the lite map composable.
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
