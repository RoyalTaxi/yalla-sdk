package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.settings.MapKind
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.CenterPinState
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapEvent
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute
import uz.yalla.maps.api.model.MapStyle
import uz.yalla.maps.config.MapFactory

private const val PROVIDER_READY_TIMEOUT_MS = 5_000L

public class SwitchingMapController internal constructor(
    private val factory: MapFactory,
    private val initialPosition: CameraPosition? = null
) : MapController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val active = MutableStateFlow<MapController?>(null)

    internal val activeBackend: StateFlow<MapController?> = active

    private val _cameraPosition = MutableStateFlow(initialPosition ?: CameraPosition.DEFAULT)
    override val cameraPosition: StateFlow<CameraPosition> = _cameraPosition

    private val _centerPin = MutableStateFlow(CenterPinState.INITIAL)
    override val centerPin: StateFlow<CenterPinState> = _centerPin

    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady

    private val _events =
        MutableSharedFlow<MapEvent>(replay = 0, extraBufferCapacity = 16, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val events: SharedFlow<MapEvent> = _events.asSharedFlow()

    private var pendingPadding: PaddingValues = PaddingValues()
    private var pendingMarkers: List<MapMarker> = emptyList()
    private var pendingRoutes: List<MapRoute> = emptyList()
    private var pendingCircles: List<MapCircle> = emptyList()
    private var interactionEnabled: Boolean = true
    private var userLocation: GeoPoint? = null
    private var userLocationEnabled = true
    private var lockedTarget: GeoPoint? = null
    private var lockedZoom: Float? = null
    private var cameraCommanded = false

    private var currentKind: MapKind? = null
    private var currentStyle: MapStyle = MapStyle.CARTO
    private var currentIsDark: Boolean = false

    private var observerJobs: List<Job> = emptyList()
    private var seedJob: Job? = null

    private var cameraSeedApplied = false

    internal suspend fun switchTo(kind: MapKind) {
        if (currentKind == kind && active.value != null) return
        seedJob?.cancel()
        val previous = active.value
        val next =
            when (kind) {
                MapKind.Google -> factory.createGoogleController()
                MapKind.Libre -> factory.createLibreController()
            }
        currentKind = kind
        val seedSource = _cameraPosition.value
        cameraSeedApplied = false
        wireObservers(next)
        active.value = next
        seedJob =
            scope.launch {
                val readied = withTimeoutOrNull(PROVIDER_READY_TIMEOUT_MS) { next.isReady.first { it } } != null
                if (active.value !== next) return@launch
                if (!readied) {
                    _events.emit(MapEvent.ProviderUnavailable)
                    return@launch
                }
                next.setDesiredPadding(pendingPadding)
                next.setMarkers(pendingMarkers)
                next.setRoutes(pendingRoutes)
                next.setCircles(pendingCircles)
                next.setInteractionEnabled(interactionEnabled)
                next.setUserLocationEnabled(userLocationEnabled)
                next.setUserLocation(userLocation)
                next.setStyle(currentStyle, currentIsDark)
                if (!cameraCommanded &&
                    seedSource.target != GeoPoint.Zero
                ) {
                    next.moveTo(seedSource.target, seedSource.zoom)
                }
                lockedTarget?.let { next.lockTarget(it, lockedZoom) }
                cameraSeedApplied = true
            }
        previous?.close()
    }

    private fun wireObservers(controller: MapController) {
        observerJobs.forEach { it.cancel() }
        var centerPinSeeded = false
        observerJobs =
            listOf(
                controller.cameraPosition
                    .onEach {
                        if (!cameraSeedApplied &&
                            it == CameraPosition.DEFAULT &&
                            _cameraPosition.value != CameraPosition.DEFAULT
                        ) {
                            return@onEach
                        }
                        _cameraPosition.value = it
                    }.launchIn(scope),
                controller.centerPin
                    .onEach {
                        if (!centerPinSeeded) {
                            centerPinSeeded = true
                            if (it == CenterPinState.INITIAL &&
                                _centerPin.value != CenterPinState.INITIAL
                            ) {
                                return@onEach
                            }
                        }
                        _centerPin.value = it
                    }.launchIn(scope),
                controller.isReady.onEach { _isReady.value = it }.launchIn(scope),
                controller.events.onEach { _events.emit(it) }.launchIn(scope)
            )
    }

    override suspend fun moveTo(
        point: GeoPoint,
        zoom: Float
    ) {
        cameraCommanded = true
        active.value?.moveTo(point, zoom)
    }

    override suspend fun animateTo(
        point: GeoPoint,
        zoom: Float,
        durationMs: Int
    ) {
        cameraCommanded = true
        active.value?.animateTo(point, zoom, durationMs)
    }

    override suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float,
        durationMs: Int
    ) {
        cameraCommanded = true
        active.value?.animateToWithBearing(point, bearing, zoom, durationMs)
    }

    override suspend fun fitBounds(
        points: List<GeoPoint>,
        animate: Boolean,
        padding: PaddingValues?
    ) {
        cameraCommanded = true
        active.value?.fitBounds(points, animate, padding)
    }

    override suspend fun zoomIn() {
        active.value?.zoomIn()
    }

    override suspend fun zoomOut() {
        active.value?.zoomOut()
    }

    override suspend fun setZoom(zoom: Float) {
        active.value?.setZoom(zoom)
    }

    override suspend fun setStyle(
        style: MapStyle,
        isDark: Boolean
    ) {
        currentStyle = style
        currentIsDark = isDark
        active.value?.setStyle(style, isDark)
    }

    override fun setDesiredPadding(padding: PaddingValues) {
        pendingPadding = padding
        active.value?.setDesiredPadding(padding)
    }

    override fun setInteractionEnabled(enabled: Boolean) {
        interactionEnabled = enabled
        active.value?.setInteractionEnabled(enabled)
    }

    override fun setMarkers(markers: List<MapMarker>) {
        pendingMarkers = markers
        active.value?.setMarkers(markers)
    }

    override fun setRoutes(routes: List<MapRoute>) {
        pendingRoutes = routes
        active.value?.setRoutes(routes)
    }

    override fun setCircles(circles: List<MapCircle>) {
        pendingCircles = circles
        active.value?.setCircles(circles)
    }

    override fun setUserLocation(point: GeoPoint?) {
        userLocation = point
        active.value?.setUserLocation(point)
    }

    override fun setUserLocationEnabled(enabled: Boolean) {
        userLocationEnabled = enabled
        active.value?.setUserLocationEnabled(enabled)
    }

    override fun lockTarget(
        point: GeoPoint,
        zoom: Float?
    ) {
        lockedTarget = point
        lockedZoom = zoom
        active.value?.lockTarget(point, zoom)
    }

    override fun unlockTarget() {
        lockedTarget = null
        lockedZoom = null
        active.value?.unlockTarget()
    }

    override fun close() {
        seedJob?.cancel()
        seedJob = null
        observerJobs.forEach { it.cancel() }
        observerJobs = emptyList()
        active.value?.close()
        active.value = null
        scope.cancel()
    }
}
