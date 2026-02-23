package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.MarkerState

interface ExtendedMap {
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
