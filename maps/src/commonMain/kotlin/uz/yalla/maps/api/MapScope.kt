package uz.yalla.maps.api

import uz.yalla.maps.compose.CameraPositionState

/**
 * Receiver scope for the custom content lambda in [ExtendedMap.Content].
 *
 * Provides access to the current [cameraState] and a flag indicating
 * which map backend is active, allowing callers to conditionally render
 * provider-specific overlays.
 *
 * @since 0.0.1
 */
interface MapScope {
    /**
     * Camera position state for the active map instance.
     *
     * @since 0.0.1
     */
    val cameraState: CameraPositionState

    /**
     * `true` when the active map backend is Google Maps; `false` for MapLibre.
     *
     * @since 0.0.1
     */
    val isGoogleMaps: Boolean
}

/**
 * Default implementation of [MapScope].
 *
 * @param cameraState Camera position state for the active map.
 * @param isGoogleMaps `true` when backed by Google Maps, `false` for MapLibre.
 * @since 0.0.1
 */
internal class MapScopeImpl(
    override val cameraState: CameraPositionState,
    override val isGoogleMaps: Boolean
) : MapScope
