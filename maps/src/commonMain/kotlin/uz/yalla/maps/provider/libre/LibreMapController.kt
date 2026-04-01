package uz.yalla.maps.provider.libre

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraState
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapController.Companion.ANIMATION_DURATION
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.config.MapConstants
import uz.yalla.maps.util.hasSameValues
import uz.yalla.maps.util.plus
import uz.yalla.maps.util.toBoundingBox
import uz.yalla.maps.util.toGeoPoint
import uz.yalla.maps.util.toPosition
import kotlin.time.Duration.Companion.milliseconds
import org.maplibre.compose.camera.CameraPosition as LibreCameraPosition

/**
 * [MapController] implementation for MapLibre GL.
 *
 * Manages camera animations, marker state, and content padding through MapLibre's
 * [CameraState]. Must be bound to a live composition via [bind] before camera
 * operations can execute.
 *
 * ## Threading
 *
 * All public methods must be called from the main thread. Suspending camera operations
 * ([moveTo], [animateTo], [fitBounds], etc.) delegate to [CameraState.animateTo] which
 * is main-safe internally.
 *
 * ## Marker sync suppression flags
 *
 * Two boolean flags coordinate marker-position updates during padding-only camera moves
 * where the visual center shifts but the user's logical position should not change:
 *
 * - **[suppressMarkerSyncUntilIdle]** — Set to `true` by [applyPaddingToCurrentCamera] when
 *   padding is applied without a user gesture. While `true`, [shouldSuppressMarkerUpdate]
 *   returns `true` for programmatic (non-user) camera moves, preventing the marker from
 *   jumping to the new visual center during the padding animation. Cleared automatically
 *   when the camera reports idle (i.e., the animation finishes).
 *
 * - **[skipNextIdleMarkerSync]** — Set to `true` alongside [suppressMarkerSyncUntilIdle]
 *   as a one-shot flag. When the camera eventually idles after a padding change,
 *   [onCameraIdle] checks this flag to skip the default idle behavior of syncing the
 *   marker to the camera center. Without this flag, the marker would snap to the
 *   post-padding center even though [suppressMarkerSyncUntilIdle] was just cleared.
 *   Consumed (set to `false`) on the first idle event after being set.
 *
 * Both flags are cleared by [onUserGesture] and [reset] to ensure a clean state when
 * the user manually interacts with the map.
 *
 * @since 0.0.1
 * @see uz.yalla.maps.provider.SwitchingMapController
 * @see uz.yalla.maps.provider.google.GoogleMapController
 */
class LibreMapController : MapController {
    private var cameraState: CameraState? = null
    private var coroutineScope: CoroutineScope? = null
    private var activeAnimationJob: Job? = null
    private var targetPadding = PaddingValues()
    private var appliedPadding = PaddingValues()

    /**
     * When `true`, programmatic (non-user) camera moves will not update the marker position.
     *
     * Set by [applyPaddingToCurrentCamera] to prevent marker jumps during padding animations.
     * Cleared when the camera reports idle or when the user initiates a gesture.
     *
     * @see skipNextIdleMarkerSync
     * @see shouldSuppressMarkerUpdate
     */
    private var suppressMarkerSyncUntilIdle = false

    /**
     * One-shot flag: when `true`, the next [onCameraIdle] call skips marker-to-center sync.
     *
     * Always set together with [suppressMarkerSyncUntilIdle] by [applyPaddingToCurrentCamera].
     * Consumed (reset to `false`) on the first idle event, ensuring that only the idle
     * immediately following a padding change is suppressed.
     *
     * @see suppressMarkerSyncUntilIdle
     */
    private var skipNextIdleMarkerSync = false

    private var programmaticTarget: io.github.dellisd.spatialk.geojson.Position? = null
    private var programmaticZoom: Double? = null
    private var queuedRecenter: RecenterRequest? = null

    private val _cameraPosition = MutableStateFlow(CameraPosition.DEFAULT)
    override val cameraPosition = _cameraPosition.asStateFlow()

    private val _markerState = MutableStateFlow(MarkerState.INITIAL)
    override val markerState = _markerState.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    override val isReady = _isReady.asStateFlow()

    /**
     * Binds this controller to a live MapLibre camera state and coroutine scope.
     *
     * @param camera The MapLibre camera state to drive.
     * @param scope Coroutine scope for launching animations.
     * @since 0.0.1
     */
    fun bind(
        camera: CameraState,
        scope: CoroutineScope
    ) {
        if (cameraState !== camera) {
            cancelActiveAnimation()
            clearProgrammaticTarget()
            queuedRecenter = null
        }
        cameraState = camera
        coroutineScope = scope
        appliedPadding = camera.position.padding
    }

    /**
     * Syncs the API-layer camera position from the MapLibre camera position.
     *
     * @param position The current MapLibre camera position.
     * @since 0.0.1
     */
    fun updateFromCamera(position: LibreCameraPosition) {
        appliedPadding = position.padding
        _cameraPosition.value =
            CameraPosition(
                target = position.target.toGeoPoint(),
                zoom = position.zoom.toFloat(),
                bearing = position.bearing.toFloat(),
                tilt = position.tilt.toFloat(),
                padding = position.padding
            )
    }

    /**
     * Called when the camera stops moving. Processes queued re-center requests and syncs state.
     *
     * @since 0.0.1
     */
    fun onCameraIdle() {
        val camera = cameraState ?: return

        queuedRecenter?.let { request ->
            queuedRecenter = null
            coroutineScope?.launch {
                camera.animateTo(
                    duration = 300.milliseconds,
                    finalPosition =
                        LibreCameraPosition(
                            target = request.target,
                            zoom = request.zoom,
                            bearing = camera.position.bearing,
                            tilt = camera.position.tilt,
                            padding = targetPadding
                        )
                )
                clearProgrammaticTarget()
                updateFromCamera(camera.position)
            }
            return
        }

        if (skipNextIdleMarkerSync) {
            skipNextIdleMarkerSync = false
            updateFromCamera(camera.position)
            return
        }

        clearProgrammaticTarget()
        syncCameraState(camera)
    }

    /**
     * Called when the user manually moves the map. Clears any programmatic target.
     *
     * @since 0.0.1
     */
    fun onUserGesture() {
        clearProgrammaticTarget()
        queuedRecenter = null
        suppressMarkerSyncUntilIdle = false
        skipNextIdleMarkerSync = false
    }

    /**
     * Returns whether the current marker sync should be suppressed (e.g., during padding-only changes).
     *
     * @param isMoving Whether the camera is currently moving.
     * @param isByUser Whether the movement was user-initiated.
     * @return `true` if the marker update should be suppressed.
     * @since 0.0.1
     */
    fun shouldSuppressMarkerUpdate(
        isMoving: Boolean,
        isByUser: Boolean
    ): Boolean {
        if (isByUser) return false
        if (!suppressMarkerSyncUntilIdle) return false
        if (!isMoving) {
            suppressMarkerSyncUntilIdle = false
        }
        return true
    }

    override suspend fun moveTo(
        point: GeoPoint,
        zoom: Float
    ) {
        val camera = cameraState ?: return
        cancelActiveAnimation()

        val target = point.toPosition()
        val clampedZoom = zoom.toDouble().clampZoom()
        setProgrammaticTarget(target, clampedZoom)

        camera.animateTo(
            duration = 1.milliseconds,
            finalPosition =
                LibreCameraPosition(
                    target = target,
                    zoom = clampedZoom,
                    padding = targetPadding
                )
        )
        updateFromCamera(camera.position)
    }

    override suspend fun animateTo(
        point: GeoPoint,
        zoom: Float,
        durationMs: Int
    ) {
        val camera = cameraState ?: return

        val target = point.toPosition()
        val clampedZoom = zoom.toDouble().clampZoom()
        setProgrammaticTarget(target, clampedZoom)

        launchAnimation {
            camera.animateTo(
                duration = durationMs.milliseconds,
                finalPosition =
                    LibreCameraPosition(
                        target = target,
                        zoom = clampedZoom,
                        padding = targetPadding
                    )
            )
        }
    }

    override suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float,
        durationMs: Int
    ) {
        val camera = cameraState ?: return

        val target = point.toPosition()
        val clampedZoom = zoom.toDouble().clampZoom()
        setProgrammaticTarget(target, clampedZoom)

        launchAnimation {
            camera.animateTo(
                duration = durationMs.milliseconds,
                finalPosition =
                    LibreCameraPosition(
                        target = target,
                        zoom = clampedZoom,
                        bearing = bearing.toDouble(),
                        padding = targetPadding
                    )
            )
        }
    }

    override suspend fun fitBounds(
        points: List<GeoPoint>,
        padding: PaddingValues,
        animate: Boolean
    ) {
        val validPoints = points.filterNot { it == GeoPoint.Zero }
        if (validPoints.isEmpty()) return

        val camera = cameraState ?: return

        if (validPoints.size == 1) {
            val singlePoint = validPoints.first()
            if (animate) {
                animateTo(singlePoint, camera.position.zoom.toFloat(), ANIMATION_DURATION)
            } else {
                moveTo(singlePoint, camera.position.zoom.toFloat())
            }
            return
        }

        clearProgrammaticTarget()
        queuedRecenter = null

        launchAnimation {
            camera.animateTo(
                duration = if (animate) ANIMATION_DURATION.milliseconds else 1.milliseconds,
                boundingBox = validPoints.toBoundingBox(),
                padding = targetPadding + padding + PaddingValues(MapConstants.DEFAULT_PADDING)
            )
        }
    }

    override suspend fun zoomIn() {
        adjustZoom(delta = 1.0)
    }

    override suspend fun zoomOut() {
        adjustZoom(delta = -1.0)
    }

    override suspend fun setZoom(zoom: Float) {
        val camera = cameraState ?: return
        clearProgrammaticTarget()
        launchAnimation {
            camera.animateTo(
                duration = ANIMATION_DURATION.milliseconds,
                finalPosition = camera.position.copy(zoom = zoom.toDouble().clampZoom(), padding = targetPadding)
            )
        }
    }

    override fun setDesiredPadding(padding: PaddingValues) {
        if (targetPadding.hasSameValues(padding)) return
        targetPadding = padding
    }

    override suspend fun updatePadding(padding: PaddingValues) {
        if (targetPadding.hasSameValues(padding) && appliedPadding.hasSameValues(padding)) return
        targetPadding = padding
        val camera = cameraState ?: return

        if (padding.hasSameValues(appliedPadding)) return

        if (programmaticTarget != null) {
            queuedRecenter = RecenterRequest(programmaticTarget!!, programmaticZoom ?: camera.position.zoom)
            if (!camera.isCameraMoving) {
                onCameraIdle()
            }
            return
        }

        applyPaddingToCurrentCamera(camera)
    }

    /**
     * Updates the desired padding without triggering a camera re-center or animation.
     *
     * @param padding The new padding value.
     * @since 0.0.1
     */
    fun updatePaddingSilently(padding: PaddingValues) {
        if (targetPadding.hasSameValues(padding)) return
        targetPadding = padding
    }

    override fun updateMarkerState(state: MarkerState) {
        _markerState.value = state
    }

    override fun setMarkerPosition(point: GeoPoint) {
        _markerState.value = _markerState.value.copy(point = point)
    }

    override fun clearMarker() {
        _markerState.value = MarkerState.INITIAL
    }

    override fun onMapReady() {
        _isReady.value = true
    }

    override fun reset() {
        cancelActiveAnimation()
        clearProgrammaticTarget()
        queuedRecenter = null
        _isReady.value = false
        _markerState.value = MarkerState.INITIAL
        _cameraPosition.value = CameraPosition.DEFAULT
        targetPadding = PaddingValues()
        appliedPadding = PaddingValues()
        suppressMarkerSyncUntilIdle = false
        skipNextIdleMarkerSync = false
    }

    private fun syncCameraState(camera: CameraState) {
        updateFromCamera(camera.position)
        updateMarkerState(
            MarkerState(
                point = camera.position.target.toGeoPoint(),
                isMoving = false,
                isByUser = false
            )
        )
    }

    private fun setProgrammaticTarget(
        target: io.github.dellisd.spatialk.geojson.Position,
        zoom: Double
    ) {
        programmaticTarget = target
        programmaticZoom = zoom
    }

    private fun clearProgrammaticTarget() {
        programmaticTarget = null
        programmaticZoom = null
    }

    private fun cancelActiveAnimation() {
        activeAnimationJob?.cancel()
        activeAnimationJob = null
    }

    private fun applyPaddingToCurrentCamera(camera: CameraState) {
        cancelActiveAnimation()
        suppressMarkerSyncUntilIdle = true
        skipNextIdleMarkerSync = true
        camera.position = camera.position.copy(padding = targetPadding)
        updateFromCamera(camera.position)
    }

    private fun launchAnimation(block: suspend () -> Unit) {
        val scope = coroutineScope ?: return
        cancelActiveAnimation()
        activeAnimationJob =
            scope.launch {
                block()
                cameraState?.let { updateFromCamera(it.position) }
                updateMarkerState(
                    MarkerState(
                        point = cameraState?.position?.target?.toGeoPoint() ?: GeoPoint.Zero,
                        isMoving = false,
                        isByUser = false
                    )
                )
            }
    }

    private suspend fun adjustZoom(delta: Double) {
        val camera = cameraState ?: return
        val newZoom = (camera.position.zoom + delta).clampZoom()
        if (newZoom != camera.position.zoom) {
            launchAnimation {
                camera.animateTo(
                    duration = ANIMATION_DURATION.milliseconds,
                    finalPosition = camera.position.copy(zoom = newZoom, padding = targetPadding)
                )
            }
        }
    }

    private fun Double.clampZoom() = coerceIn(MapConstants.ZOOM_MIN, MapConstants.ZOOM_MAX)
}

private data class RecenterRequest(
    val target: io.github.dellisd.spatialk.geojson.Position,
    val zoom: Double
)
