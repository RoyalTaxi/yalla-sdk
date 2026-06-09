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

class SwitchingMapController internal constructor(
    private val factory: MapFactory,
    private val initialPosition: CameraPosition? = null
) : MapController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val active = MutableStateFlow<MapController?>(null)

    val activeBackend: StateFlow<MapController?> = active

    private val _cameraPosition = MutableStateFlow(initialPosition ?: CameraPosition.DEFAULT)
    override val cameraPosition: StateFlow<CameraPosition> = _cameraPosition

    private val _centerPin = MutableStateFlow(CenterPinState.INITIAL)
    override val centerPin: StateFlow<CenterPinState> = _centerPin

    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady

    private val _events = MutableSharedFlow<MapEvent>(replay = 0, extraBufferCapacity = 16, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val events: SharedFlow<MapEvent> = _events.asSharedFlow()

    private var pendingPadding: PaddingValues = PaddingValues()
    private var pendingMarkers: List<MapMarker> = emptyList()
    private var pendingRoutes: List<MapRoute> = emptyList()
    private var pendingCircles: List<MapCircle> = emptyList()
    private var lockedTarget: GeoPoint? = null
    private var lockedZoom: Float? = null

    private var currentKind: MapKind? = null
    private var currentStyle: MapStyle = MapStyle.CARTO
    private var currentIsDark: Boolean = false

    private var observerJobs: List<Job> = emptyList()

    internal suspend fun switchTo(kind: MapKind) {
        if (currentKind == kind && active.value != null) return
        val previous = active.value
        val snapshot = previous?.snapshotScene()
        val next = when (kind) {
            MapKind.Google -> factory.createGoogleController()
            MapKind.Libre -> factory.createLibreController()
        }
        currentKind = kind
        val seedSource = snapshot?.cameraPosition ?: _cameraPosition.value
        wireObservers(next)
        active.value = next
        if (snapshot != null) {
            applySnapshot(next, snapshot)
        } else if (seedSource.target != GeoPoint.Zero) {
            scope.launch {
                next.isReady.first { it }
                next.moveTo(seedSource.target, seedSource.zoom)
            }
        }
        previous?.close()
    }

    internal suspend fun applyStyle(style: MapStyle, isDark: Boolean) {
        currentStyle = style
        currentIsDark = isDark
        active.value?.setStyle(style, isDark)
    }

    private fun wireObservers(controller: MapController) {
        observerJobs.forEach { it.cancel() }
        observerJobs = listOf(
            controller.cameraPosition.onEach { _cameraPosition.value = it }.launchIn(scope),
            controller.centerPin.onEach { _centerPin.value = it }.launchIn(scope),
            controller.isReady.onEach { _isReady.value = it }.launchIn(scope),
            controller.events.onEach { _events.emit(it) }.launchIn(scope)
        )
    }

    private suspend fun applySnapshot(controller: MapController, snapshot: MapController.SceneSnapshot) {
        controller.setDesiredPadding(snapshot.padding)
        controller.setMarkers(snapshot.markers)
        controller.setRoutes(snapshot.routes)
        controller.setCircles(snapshot.circles)
        controller.setStyle(currentStyle, currentIsDark)
        controller.moveTo(snapshot.cameraPosition.target, snapshot.cameraPosition.zoom)
        snapshot.lockedTarget?.let { controller.lockTarget(it, snapshot.lockedZoom) }
    }

    override suspend fun moveTo(point: GeoPoint, zoom: Float) {
        active.value?.moveTo(point, zoom)
    }

    override suspend fun animateTo(point: GeoPoint, zoom: Float, durationMs: Int) {
        active.value?.animateTo(point, zoom, durationMs)
    }

    override suspend fun animateToWithBearing(point: GeoPoint, bearing: Float, zoom: Float, durationMs: Int) {
        active.value?.animateToWithBearing(point, bearing, zoom, durationMs)
    }

    override suspend fun fitBounds(points: List<GeoPoint>, animate: Boolean, padding: PaddingValues?) {
        active.value?.fitBounds(points, animate, padding)
    }

    override suspend fun zoomIn() { active.value?.zoomIn() }

    override suspend fun zoomOut() { active.value?.zoomOut() }

    override suspend fun setZoom(zoom: Float) { active.value?.setZoom(zoom) }

    override suspend fun setStyle(style: MapStyle, isDark: Boolean) {
        currentStyle = style
        currentIsDark = isDark
        active.value?.setStyle(style, isDark)
    }

    override fun setDesiredPadding(padding: PaddingValues) {
        pendingPadding = padding
        active.value?.setDesiredPadding(padding)
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

    override fun lockTarget(point: GeoPoint, zoom: Float?) {
        lockedTarget = point
        lockedZoom = zoom
        active.value?.lockTarget(point, zoom)
    }

    override fun unlockTarget() {
        lockedTarget = null
        lockedZoom = null
        active.value?.unlockTarget()
    }

    override fun snapshotScene(): MapController.SceneSnapshot = active.value?.snapshotScene()
        ?: MapController.SceneSnapshot(
            cameraPosition = _cameraPosition.value,
            markers = pendingMarkers,
            routes = pendingRoutes,
            circles = pendingCircles,
            padding = pendingPadding,
            lockedTarget = lockedTarget,
            lockedZoom = lockedZoom
        )

    override fun close() {
        observerJobs.forEach { it.cancel() }
        observerJobs = emptyList()
        active.value?.close()
        active.value = null
        scope.cancel()
    }
}
