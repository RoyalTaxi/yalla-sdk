package uz.yalla.maps.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.core.geo.GeoPoint

interface StaticMap {
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
