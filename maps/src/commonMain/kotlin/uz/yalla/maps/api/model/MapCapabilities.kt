package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
public data class MapCapabilities(
    val supportsOffline: Boolean = false,
    val supports3D: Boolean = false,
    val supportsTraffic: Boolean = false,
    val supportsStreetView: Boolean = false,
    val supportsCustomStyles: Boolean = true,
    val supportsRotation: Boolean = false,
    val supportsTilt: Boolean = false,
    val supportsCircles: Boolean = true,
    val supportsRoutePatterns: Boolean = true,
    val maxZoom: Float = 21f,
    val minZoom: Float = 1f
) {
    public companion object {
        public val LIBRE: MapCapabilities =
            MapCapabilities(
                supportsOffline = true,
                supportsCustomStyles = true,
                supportsRotation = true,
                supportsTilt = true,
                supportsCircles = true,
                supportsRoutePatterns = true,
                maxZoom = 21f,
                minZoom = 4f
            )

        public val GOOGLE: MapCapabilities =
            MapCapabilities(
                supportsTraffic = true,
                supportsRotation = true,
                supportsTilt = true,
                supportsCustomStyles = false,
                supportsCircles = true,
                supportsRoutePatterns = true,
                maxZoom = 21f,
                minZoom = 4f
            )
    }
}
