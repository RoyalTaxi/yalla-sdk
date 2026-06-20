package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

// TODO(quality, needs-decision): finding #7 — MapCapabilities is dead public API: no controller
// returns it and nothing reads it (the only reference is an iOS NSLog string). It should either be
// made load-bearing (a MapController.capabilities property each backend returns) or deleted, but it
// is frozen in the committed `.api`/`.klib.api` dumps, so deletion is a breaking dump change. Needs
// owner sign-off on a binary-API break (and/or a product decision on capability negotiation).

/** A declared per-backend capability matrix. Currently decorative — see the TODO above. */
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
