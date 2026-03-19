package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.config.MapConstants

/**
 * Reactive controller for programmatic map camera and marker manipulation.
 *
 * Exposes camera position, marker state, and readiness as [StateFlow]s so the UI
 * layer can observe changes reactively. All camera-movement functions are suspending
 * to allow callers to sequence animations.
 *
 * Concrete implementations exist for each backend: [GoogleMapController][uz.yalla.maps.provider.google.GoogleMapController]
 * and [LibreMapController][uz.yalla.maps.provider.libre.LibreMapController].
 * [SwitchingMapController][uz.yalla.maps.provider.SwitchingMapController] delegates
 * to the active backend at runtime.
 *
 * ## Usage
 *
 * ```kotlin
 * val controller: MapController = koinInject()
 * LaunchedEffect(Unit) {
 *     controller.animateTo(point = myLocation, zoom = 16f)
 * }
 * ```
 *
 * @since 0.0.1
 */
interface MapController {
    /**
     * Current camera position as a reactive flow.
     *
     * @since 0.0.1
     */
    val cameraPosition: StateFlow<CameraPosition>

    /**
     * Current center-screen marker state as a reactive flow.
     *
     * @since 0.0.1
     */
    val markerState: StateFlow<MarkerState>

    /**
     * Whether the underlying map view has finished loading.
     *
     * @since 0.0.1
     */
    val isReady: StateFlow<Boolean>

    /**
     * Instantly moves the camera to the given [point] without animation.
     *
     * @param point Target geographic coordinate.
     * @param zoom Desired zoom level (clamped to provider limits).
     * @since 0.0.1
     */
    suspend fun moveTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat()
    )

    /**
     * Smoothly animates the camera to the given [point].
     *
     * @param point Target geographic coordinate.
     * @param zoom Desired zoom level (clamped to provider limits).
     * @param durationMs Animation duration in milliseconds.
     * @since 0.0.1
     */
    suspend fun animateTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    /**
     * Smoothly animates the camera to the given [point] with a specific [bearing].
     *
     * @param point Target geographic coordinate.
     * @param bearing Direction the camera faces, in degrees clockwise from north.
     * @param zoom Desired zoom level (clamped to provider limits).
     * @param durationMs Animation duration in milliseconds.
     * @since 0.0.1
     */
    suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    /**
     * Adjusts the camera to fit all [points] within the visible viewport.
     *
     * @param points Geographic coordinates to include in the viewport.
     * @param padding Extra padding around the bounds.
     * @param animate Whether to animate the transition.
     * @since 0.0.1
     */
    suspend fun fitBounds(
        points: List<GeoPoint>,
        padding: PaddingValues = PaddingValues(),
        animate: Boolean = true
    )

    /**
     * Increases the zoom level by one step.
     *
     * @since 0.0.1
     */
    suspend fun zoomIn()

    /**
     * Decreases the zoom level by one step.
     *
     * @since 0.0.1
     */
    suspend fun zoomOut()

    /**
     * Sets the zoom level to an exact value.
     *
     * @param zoom Target zoom level (clamped to provider limits).
     * @since 0.0.1
     */
    suspend fun setZoom(zoom: Float)

    /**
     * Sets the desired content padding without triggering a camera re-center.
     *
     * @param padding The desired padding around the map content area.
     * @since 0.0.1
     */
    fun setDesiredPadding(padding: PaddingValues)

    /**
     * Updates the content padding and re-centers the camera if a programmatic target is active.
     *
     * @param padding The new padding to apply.
     * @since 0.0.1
     */
    suspend fun updatePadding(padding: PaddingValues)

    /**
     * Replaces the current marker state with [state].
     *
     * @param state The new marker state.
     * @since 0.0.1
     */
    fun updateMarkerState(state: MarkerState)

    /**
     * Updates only the marker position, preserving movement and user flags.
     *
     * @param point The new marker coordinate.
     * @since 0.0.1
     */
    fun setMarkerPosition(point: GeoPoint)

    /**
     * Resets the marker to [MarkerState.INITIAL].
     *
     * @since 0.0.1
     */
    fun clearMarker()

    /**
     * Signals that the underlying map view has finished loading.
     *
     * @since 0.0.1
     */
    fun onMapReady()

    /**
     * Resets all controller state (camera, marker, padding, animations) to defaults.
     *
     * @since 0.0.1
     */
    fun reset()

    /**
     * Releases resources held by this controller. Called when the controller is no longer needed.
     *
     * @since 0.0.1
     */
    fun close() {}

    companion object {
        /**
         * Default animation duration in milliseconds for camera transitions.
         *
         * @since 0.0.1
         */
        const val ANIMATION_DURATION = 1000
    }
}
