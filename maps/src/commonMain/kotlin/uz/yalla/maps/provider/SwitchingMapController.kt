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
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.settings.MapKind
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.CenterPinState
import uz.yalla.maps.config.requireMaps
import uz.yalla.maps.util.hasSameValues

class SwitchingMapController : MapController {
    private val config = requireMaps()
    private val _googleController = lazy { requireFactory().createGoogleProvider().createController() }
    private val _libreController = lazy { requireFactory().createLibreProvider().createController() }

    val googleController: MapController by _googleController

    val libreController: MapController by _libreController

    private fun requireFactory() = requireNotNull(config.factory) { "MapFactory not installed" }

    private val scope = CoroutineScope(config.scope.coroutineContext + SupervisorJob())
    private var activeController: MapController? = null
    private val currentController: MapController get() = activeController ?: googleController
    private var collectorJob: Job? = null
    private var handoffJob: Job? = null
    private var suppressStateSync: Boolean = false
    private var desiredPadding: PaddingValues = PaddingValues()

    var isClosed: Boolean = false
        private set

    private val _cameraPosition = MutableStateFlow(CameraPosition.DEFAULT)
    override val cameraPosition = _cameraPosition.asStateFlow()

    private val _centerPin = MutableStateFlow(CenterPinState.INITIAL)
    override val centerPin = _centerPin.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    override val isReady = _isReady.asStateFlow()

    init {
        scope.launch {
            config.mapKindPreference.collectLatest { type ->
                val nextController = when (type) {
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

    override fun updateCenterPin(state: CenterPinState) = currentController.updateCenterPin(state)

    override fun setCenterPin(point: GeoPoint) = currentController.setCenterPin(point)

    override fun clearCenterPin() = currentController.clearCenterPin()

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
        _centerPin.value = CenterPinState.INITIAL
        _cameraPosition.value = CameraPosition.DEFAULT
    }

    private fun collectFromActive() {
        collectorJob?.cancel()
        collectorJob = scope.launch {
            launch { currentController.cameraPosition.collectLatest(::updateCameraFromController) }
            launch { currentController.centerPin.collectLatest(::updateCenterPinFromController) }
            launch { currentController.isReady.collectLatest { _isReady.value = it } }
        }
    }

    private fun syncFromActive() {
        updateCameraFromController(currentController.cameraPosition.value)
        updateCenterPinFromController(currentController.centerPin.value)
        _isReady.value = currentController.isReady.value
    }

    private fun seedNextController(nextController: MapController) {
        handoffJob?.cancel()

        val preservedCamera = _cameraPosition.value
        val currentMarker = _centerPin.value
        val hasPreservedCamera = preservedCamera.target != GeoPoint.Zero ||
            preservedCamera.zoom != CameraPosition.DEFAULT.zoom ||
            preservedCamera.bearing != 0f ||
            preservedCamera.tilt != 0f
        val hasPreservedState = hasPreservedCamera || currentMarker != CenterPinState.INITIAL
        suppressStateSync = hasPreservedState
        nextController.setDesiredPadding(
            if (desiredPadding.hasSameValues(PaddingValues())) preservedCamera.padding else desiredPadding
        )

        if (currentMarker != CenterPinState.INITIAL) {
            nextController.updateCenterPin(currentMarker)
        }

        if (!hasPreservedState) return

        handoffJob = scope.launch {
            withTimeoutOrNull(PROVIDER_READY_TIMEOUT_MS) {
                nextController.isReady
                    .filter { it }
                    .first()
            }

            val handoffPadding = if (desiredPadding.hasSameValues(PaddingValues())) preservedCamera.padding else desiredPadding

            nextController.setDesiredPadding(handoffPadding)
            nextController.updatePadding(handoffPadding)

            if (hasPreservedCamera) {
                nextController.moveTo(preservedCamera.target, preservedCamera.zoom)
            }
            if (currentMarker != CenterPinState.INITIAL) {
                nextController.updateCenterPin(currentMarker.copy(isMoving = false))
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

    private fun updateCenterPinFromController(centerPin: CenterPinState) {
        if (suppressStateSync) return
        val current = _centerPin.value
        if (centerPin == CenterPinState.INITIAL && current != CenterPinState.INITIAL) return
        _centerPin.value = centerPin
    }

    companion object {
        const val PROVIDER_READY_TIMEOUT_MS = 5_000L
    }
}
