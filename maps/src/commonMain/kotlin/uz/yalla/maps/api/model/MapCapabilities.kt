package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

/**
 * Describes the feature set supported by a specific map backend.
 *
 * Use this to query whether a provider supports features like offline maps,
 * traffic layers, or custom styles before enabling them in the UI.
 */
@Immutable
data class MapCapabilities(
    val supportsOffline: Boolean = false,
    val supports3D: Boolean = false,
    val supportsTraffic: Boolean = false,
    val supportsStreetView: Boolean = false,
    val supportsCustomStyles: Boolean = true,
    val supportsRotation: Boolean = false,
    val supportsTilt: Boolean = false,
    val maxZoom: Float = 21f,
    val minZoom: Float = 1f
) {
    companion object {
        /**
         * Capabilities for the MapLibre backend.
         */
        val LIBRE =
            MapCapabilities(
                supportsOffline = true,
                supportsCustomStyles = true,
                supportsRotation = true,
                supportsTilt = true,
                maxZoom = 21f,
                minZoom = 4f
            )

        /**
         * Capabilities for the Google Maps backend.
         */
        val GOOGLE =
            MapCapabilities(
                supportsTraffic = true,
                supportsRotation = true,
                supportsTilt = true,
                supportsCustomStyles = false,
                maxZoom = 21f,
                minZoom = 4f
            )
    }
}
