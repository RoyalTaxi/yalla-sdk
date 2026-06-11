package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
data class MapCapabilities(
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
    companion object {
        val LIBRE = MapCapabilities(
            supportsOffline = true,
            supportsCustomStyles = true,
            supportsRotation = true,
            supportsTilt = true,
            supportsCircles = true,
            supportsRoutePatterns = true,
            maxZoom = 21f,
            minZoom = 4f
        )

        val GOOGLE = MapCapabilities(
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
