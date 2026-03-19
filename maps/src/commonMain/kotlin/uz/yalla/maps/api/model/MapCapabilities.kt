package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

/**
 * Describes the feature set supported by a specific map backend.
 *
 * Use this to query whether a provider supports features like offline maps,
 * traffic layers, or custom styles before enabling them in the UI.
 *
 * @property supportsOffline Whether the provider supports offline map tiles.
 * @property supports3D Whether 3D building extrusion is available.
 * @property supportsTraffic Whether real-time traffic overlay is available.
 * @property supportsStreetView Whether street-level imagery is available.
 * @property supportsCustomStyles Whether custom map style JSON is supported.
 * @property supportsRotation Whether bearing/rotation gestures are supported.
 * @property supportsTilt Whether tilt/perspective gestures are supported.
 * @property maxZoom Maximum allowed zoom level.
 * @property minZoom Minimum allowed zoom level.
 * @since 0.0.1
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
         *
         * @since 0.0.1
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
         *
         * @since 0.0.1
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
