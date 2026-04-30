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
 */
interface ExtendedMap {
    /**
     * Renders the extended map composable.
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
