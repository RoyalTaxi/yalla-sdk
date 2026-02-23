package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.MarkerState

interface LiteMap {
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
