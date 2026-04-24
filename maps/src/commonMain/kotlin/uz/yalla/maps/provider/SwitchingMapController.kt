package uz.yalla.maps.provider

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.settings.MapKind
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.provider.google.GoogleMapController
import uz.yalla.maps.provider.libre.LibreMapController
import uz.yalla.maps.util.hasSameValues

/**
 * [MapController] that delegates to Google or Libre at runtime based on user preference.
 *
 * Maintains both a [googleController] and [libreController] lazily, forwarding
 * all operations to whichever is currently active. When the user switches providers,
 * the preserved camera position and marker state are handed off to the new backend.
 *
 * ## State preservation during provider switch
 *
 * When the active map provider changes, the following state is preserved and transferred
 * to the new backend:
 *
 * - **Camera position** — target coordinate, zoom level, bearing, and tilt are captured
 *   from the outgoing controller and applied to the incoming one once it reports [isReady].
 * - **Content padding** — the last [setDesiredPadding] value is forwarded immediately;
 *   an [updatePadding] call is issued after readiness to re-center correctly.
 * - **Marker state** — the current [MarkerState] (position, visibility) is applied to the
 *   incoming controller. The `isMoving` flag is reset to `false` on handoff.
 *
 * State that is **not** preserved: active animations (they are cancelled), provider-specific
 * internal state (e.g., Google's content-padding flow or Libre's programmatic-target tracking).
 *
 * The handoff waits up to [PROVIDER_READY_TIMEOUT_MS] milliseconds for the new backend to
 * become ready. If the timeout elapses, state is applied optimistically.
 *
 * ## Lifecycle
 *
 * The controller does not own its coroutine scope. A supervised child scope is derived
 * from [scope] so that cancelling [scope] automatically stops all internal coroutines.
 * Call [close] when the controller is permanently discarded to cancel the child scope and
 * release both backend controllers early. After [close] is called, calling any other method
 * is a no-op; [isClosed] returns `true`.
 *
 * @param interfacePreferences Source of the user's map provider preference.
 * @param scope Caller-owned parent scope. The controller derives a supervised child from
 *   this scope; cancelling [scope] propagates to the controller automatically.
 * @since 0.0.1
 * @see SwitchingMapProvider
 * @see GoogleMapController
 * @see LibreMapController
 */
class SwitchingMapController(
    interfacePreferences: InterfacePreferences,
    scope: CoroutineScope,
) : MapController {
    private val _googleController = lazy { GoogleMapController() }
    private val _libreController = lazy { LibreMapController() }

    /**
     * Lazily initialized Google Maps controller, exposed for provider-specific access.
     *
     * @since 0.0.1
     */
    val googleController by _googleController

    /**
     * Lazily initialized MapLibre controller, exposed for provider-specific access.
     *
     * @since 0.0.1
     */
    val libreController by _libreController

    private val scope = CoroutineScope(scope.coroutineContext + SupervisorJob())
    private var activeController: MapController? = null
    private val currentController: MapController get() = activeController ?: googleController
    private var collectorJob: Job? = null
    private var handoffJob: Job? = null
    private var suppressStateSync: Boolean = false
    private var desiredPadding: PaddingValues = PaddingValues()

    /**
     * `true` after [close] has been called. Once closed, all mutating operations become no-ops.
     *
     * @since 0.0.1
     */
    var isClosed: Boolean = false
        private set

    private val _cameraPosition = MutableStateFlow(CameraPosition.DEFAULT)
    override val cameraPosition = _cameraPosition.asStateFlow()

    private val _markerState = MutableStateFlow(MarkerState.INITIAL)
    override val markerState = _markerState.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    override val isReady = _isReady.asStateFlow()

    init {
        scope.launch {
            interfacePreferences.mapKind.collectLatest { type ->
                val nextController =
                    when (type) {
                        MapKind.Google -> googleController
                        MapKind.Libre -> libreController
                    }
                seedNextController(nextController)
                activeController = nextController
                syncFromActive()
                collectFromActive()
            }
        }
    }

    override suspend fun moveTo(
        point: GeoPoint,
        zoom: Float
    ) {
        currentController.moveTo(point, zoom)
    }

    override suspend fun animateTo(
        point: GeoPoint,
        zoom: Float,
        durationMs: Int
    ) {
        currentController.animateTo(point, zoom, durationMs)
    }

    override suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float,
        durationMs: Int
    ) {
        currentController.animateToWithBearing(point, bearing, zoom, durationMs)
    }

    override suspend fun fitBounds(
        points: List<GeoPoint>,
        padding: PaddingValues,
        animate: Boolean
    ) {
        currentController.fitBounds(points, padding, animate)
    }

    override suspend fun zoomIn() = currentController.zoomIn()

    override suspend fun zoomOut() = currentController.zoomOut()

    override suspend fun setZoom(zoom: Float) = currentController.setZoom(zoom)

    override fun setDesiredPadding(padding: PaddingValues) {
        desiredPadding = padding
        currentController.setDesiredPadding(padding)
    }

    override suspend fun updatePadding(padding: PaddingValues) {
        desiredPadding = padding
        currentController.setDesiredPadding(padding)
        currentController.updatePadding(padding)
    }

    override fun updateMarkerState(state: MarkerState) = currentController.updateMarkerState(state)

    override fun setMarkerPosition(point: GeoPoint) = currentController.setMarkerPosition(point)

    override fun clearMarker() = currentController.clearMarker()

    override fun onMapReady() = currentController.onMapReady()

    override fun close() {
        if (isClosed) return
        isClosed = true
        collectorJob?.cancel()
        handoffJob?.cancel()
        scope.cancel()
        if (_googleController.isInitialized()) googleController.reset()
        if (_libreController.isInitialized()) libreController.reset()
    }

    override fun reset() {
        if (isClosed) return
        collectorJob?.cancel()
        handoffJob?.cancel()
        suppressStateSync = false
        if (_googleController.isInitialized()) googleController.reset()
        if (_libreController.isInitialized()) libreController.reset()
        _isReady.value = false
        _markerState.value = MarkerState.INITIAL
        _cameraPosition.value = CameraPosition.DEFAULT
    }

    private fun collectFromActive() {
        collectorJob?.cancel()
        collectorJob =
            scope.launch {
                launch { currentController.cameraPosition.collectLatest(::updateCameraFromController) }
                launch { currentController.markerState.collectLatest(::updateMarkerFromController) }
                launch { currentController.isReady.collectLatest { _isReady.value = it } }
            }
    }

    private fun syncFromActive() {
        updateCameraFromController(currentController.cameraPosition.value)
        updateMarkerFromController(currentController.markerState.value)
        _isReady.value = currentController.isReady.value
    }

    private fun seedNextController(nextController: MapController) {
        handoffJob?.cancel()

        val preservedCamera = _cameraPosition.value
        val currentMarker = _markerState.value
        val hasPreservedCamera =
            preservedCamera.target != GeoPoint.Zero ||
                preservedCamera.zoom != CameraPosition.DEFAULT.zoom ||
                preservedCamera.bearing != 0f ||
                preservedCamera.tilt != 0f
        val hasPreservedState = hasPreservedCamera || currentMarker != MarkerState.INITIAL
        suppressStateSync = hasPreservedState
        nextController.setDesiredPadding(
            if (desiredPadding.hasSameValues(PaddingValues())) preservedCamera.padding else desiredPadding
        )

        if (currentMarker != MarkerState.INITIAL) {
            nextController.updateMarkerState(currentMarker)
        }

        if (!hasPreservedState) return

        handoffJob =
            scope.launch {
                // Wait until the next provider map is ready before applying preserved camera/marker.
                withTimeoutOrNull(PROVIDER_READY_TIMEOUT_MS) {
                    nextController.isReady
                        .filter { it }
                        .first()
                }

                val handoffPadding =
                    if (desiredPadding.hasSameValues(PaddingValues())) preservedCamera.padding else desiredPadding

                nextController.setDesiredPadding(handoffPadding)
                nextController.updatePadding(handoffPadding)

                if (hasPreservedCamera) {
                    nextController.moveTo(preservedCamera.target, preservedCamera.zoom)
                }
                if (currentMarker != MarkerState.INITIAL) {
                    nextController.updateMarkerState(currentMarker.copy(isMoving = false))
                }

                suppressStateSync = false
                syncFromActive()
            }
    }

    private fun updateCameraFromController(cameraPosition: CameraPosition) {
        if (suppressStateSync) return
        val current = _cameraPosition.value
        if (cameraPosition == CameraPosition.DEFAULT && current != CameraPosition.DEFAULT) return
        _cameraPosition.value = cameraPosition
    }

    private fun updateMarkerFromController(markerState: MarkerState) {
        if (suppressStateSync) return
        val current = _markerState.value
        if (markerState == MarkerState.INITIAL && current != MarkerState.INITIAL) return
        _markerState.value = markerState
    }

    companion object {
        /**
         * Maximum time in milliseconds to wait for the incoming map provider to report
         * [MapController.isReady] during a provider switch.
         *
         * If the timeout elapses, the preserved camera position and marker state are applied
         * optimistically — the new backend will pick them up once it finishes loading.
         *
         * @since 0.0.1
         */
        const val PROVIDER_READY_TIMEOUT_MS = 5_000L
    }
}
