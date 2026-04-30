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
 * Concrete implementations exist for each backend:
 * [GoogleMapController][uz.yalla.maps.provider.google.GoogleMapController]
 * and [LibreMapController][uz.yalla.maps.provider.libre.LibreMapController].
 * [SwitchingMapController][uz.yalla.maps.provider.SwitchingMapController] delegates
 * to the active backend at runtime.
 *
 * ## Threading
 *
 * All methods must be called from the **main thread**. Suspending camera operations
 * are safe to call from any dispatcher but internally dispatch to main.
 *
 * ## Lifecycle
 *
 * 1. Create a controller via [MapProvider.createController][uz.yalla.maps.api.MapProvider.createController].
 * 2. Pass it to a map composable — the composable binds platform state internally.
 * 3. Observe [isReady] before issuing camera commands; commands issued before readiness
 *    are silently ignored.
 * 4. Call [reset] to return to default state (e.g., when navigating away).
 * 5. Call [close] when the controller is permanently discarded to release internal resources.
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
 * @see uz.yalla.maps.api.MapProvider
 */
interface MapController {
    /**
     * Current camera position as a reactive flow.
     */
    val cameraPosition: StateFlow<CameraPosition>

    /**
     * Current center-screen marker state as a reactive flow.
     */
    val markerState: StateFlow<MarkerState>

    /**
     * Whether the underlying map view has finished loading.
     */
    val isReady: StateFlow<Boolean>

    /**
     * Instantly moves the camera to the given [point] without animation.
     */
    suspend fun moveTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat()
    )

    /**
     * Smoothly animates the camera to the given [point].
     */
    suspend fun animateTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    /**
     * Smoothly animates the camera to the given [point] with a specific [bearing].
     */
    suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    /**
     * Adjusts the camera to fit all [points] within the visible viewport.
     */
    suspend fun fitBounds(
        points: List<GeoPoint>,
        padding: PaddingValues = PaddingValues(),
        animate: Boolean = true
    )

    /**
     * Increases the zoom level by one step.
     */
    suspend fun zoomIn()

    /**
     * Decreases the zoom level by one step.
     */
    suspend fun zoomOut()

    /**
     * Sets the zoom level to an exact value.
     */
    suspend fun setZoom(zoom: Float)

    /**
     * Sets the desired content padding without triggering a camera re-center.
     */
    fun setDesiredPadding(padding: PaddingValues)

    /**
     * Updates the content padding and re-centers the camera if a programmatic target is active.
     */
    suspend fun updatePadding(padding: PaddingValues)

    /**
     * Replaces the current marker state with [state].
     */
    fun updateMarkerState(state: MarkerState)

    /**
     * Updates only the marker position, preserving movement and user flags.
     */
    fun setMarkerPosition(point: GeoPoint)

    /**
     * Resets the marker to [MarkerState.INITIAL].
     */
    fun clearMarker()

    /**
     * Signals that the underlying map view has finished loading.
     */
    fun onMapReady()

    /**
     * Resets all controller state (camera, marker, padding, animations) to defaults.
     */
    fun reset()

    /**
     * Releases resources held by this controller. Called when the controller is no longer needed.
     */
    fun close() {}

    companion object {
        /**
         * Default animation duration in milliseconds for camera transitions.
         */
        const val ANIMATION_DURATION = 1000
    }
}
