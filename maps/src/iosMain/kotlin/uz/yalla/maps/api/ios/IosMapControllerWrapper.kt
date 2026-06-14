package uz.yalla.maps.api.ios

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.UIKit.UIViewController
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.IosMapController
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.CenterPinState
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapEvent
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute
import uz.yalla.maps.api.model.MapStyle

internal class IosMapControllerWrapper(
    private val renderer: IosMapRenderer
) : MapController,
    IosMapController {
    private val _cameraPosition = MutableStateFlow(CameraPosition.DEFAULT)
    override val cameraPosition = _cameraPosition.asStateFlow()

    private val _centerPin = MutableStateFlow(CenterPinState.INITIAL)
    override val centerPin = _centerPin.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    override val isReady = _isReady.asStateFlow()

    private val _events =
        MutableSharedFlow<MapEvent>(replay = 0, extraBufferCapacity = 16, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val events = _events.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var pendingPadding: PaddingValues = PaddingValues()
    private var pendingMarkers: List<MapMarker> = emptyList()
    private var pendingRoutes: List<MapRoute> = emptyList()
    private var pendingCircles: List<MapCircle> = emptyList()
    private var lastEmittedCamera: CameraPosition? = null
    private var closed = false
    private var lockedTarget: GeoPoint? = null
    private var lockedZoom: Float? = null
    private var userLocation: GeoPoint? = null
    private var userLocationEnabled = true

    private val internalListener =
        object : IosMapListener {
            override fun onCameraMove(
                target: GeoPoint,
                zoom: Float,
                bearing: Float,
                tilt: Float,
                isByUser: Boolean
            ) {
                emitCamera(target, zoom, bearing, tilt)
                _centerPin.value = _centerPin.value.copy(point = target, isMoving = true, isByUser = isByUser)
                if (isByUser) {
                    lockedTarget = null
                    lockedZoom = null
                }
            }

            override fun onCameraIdle(
                target: GeoPoint,
                zoom: Float,
                bearing: Float,
                tilt: Float,
                isByUser: Boolean
            ) {
                emitCamera(target, zoom, bearing, tilt)
                _centerPin.value = CenterPinState(point = target, isMoving = false, isByUser = isByUser)
            }

            override fun onReady() {
                _isReady.value = true
            }

            override fun onMarkerTapped(id: String) {
                scope.launch { _events.emit(MapEvent.MarkerTapped(id)) }
            }

            override fun onMapTapped(point: GeoPoint) {
                scope.launch { _events.emit(MapEvent.MapTapped(point)) }
            }

            override fun onMapLongPressed(point: GeoPoint) {
                scope.launch { _events.emit(MapEvent.MapLongPressed(point)) }
            }
        }

    init {
        renderer.setListener(internalListener)
    }

    override fun createViewController(): UIViewController = renderer.createViewController()

    override suspend fun moveTo(
        point: GeoPoint,
        zoom: Float
    ) {
        if (closed) return
        renderer.moveTo(point, zoom)
    }

    override suspend fun animateTo(
        point: GeoPoint,
        zoom: Float,
        durationMs: Int
    ) {
        if (closed) return
        renderer.animateTo(point, zoom, durationMs)
    }

    override suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float,
        durationMs: Int
    ) {
        if (closed) return
        renderer.animateToWithBearing(point, bearing, zoom, durationMs)
    }

    override suspend fun fitBounds(
        points: List<GeoPoint>,
        animate: Boolean,
        padding: PaddingValues?
    ) {
        if (closed) return
        val effective = padding ?: pendingPadding
        renderer.fitBounds(
            points = points,
            leftPt = effective.calculateLeftPadding(LayoutDirection.Ltr).value,
            topPt = effective.calculateTopPadding().value,
            rightPt = effective.calculateRightPadding(LayoutDirection.Ltr).value,
            bottomPt = effective.calculateBottomPadding().value,
            animate = animate
        )
    }

    override suspend fun zoomIn() {
        if (closed) return
        renderer.zoomIn()
    }

    override suspend fun zoomOut() {
        if (closed) return
        renderer.zoomOut()
    }

    override suspend fun setZoom(zoom: Float) {
        if (closed) return
        renderer.setZoom(zoom)
    }

    override suspend fun setStyle(
        style: MapStyle,
        isDark: Boolean
    ) {
        if (closed) return
        renderer.setColorScheme(isDark)
        when (style) {
            is MapStyle.Url -> renderer.setStyleUrl(if (isDark) style.darkUrl else style.lightUrl)
            is MapStyle.InlineJson -> renderer.setStyleJson(if (isDark) style.darkJson else style.lightJson)
            MapStyle.PlatformDefault -> Unit
        }
    }

    override fun setDesiredPadding(padding: PaddingValues) {
        if (closed) return
        pendingPadding = padding
        renderer.setPaddingPt(
            leftPt = padding.calculateLeftPadding(LayoutDirection.Ltr).value,
            topPt = padding.calculateTopPadding().value,
            rightPt = padding.calculateRightPadding(LayoutDirection.Ltr).value,
            bottomPt = padding.calculateBottomPadding().value
        )
        replayLockedTarget()
    }

    override fun setInteractionEnabled(enabled: Boolean) {
        if (closed) return
        renderer.setInteractionEnabled(enabled)
    }

    override fun setMarkers(markers: List<MapMarker>) {
        if (closed) return
        pendingMarkers = markers
        renderer.setMarkers(markers)
    }

    override fun setRoutes(routes: List<MapRoute>) {
        if (closed) return
        pendingRoutes = routes
        renderer.setRoutes(routes)
    }

    override fun setCircles(circles: List<MapCircle>) {
        if (closed) return
        pendingCircles = circles
        renderer.setCircles(circles)
    }

    override fun setUserLocation(point: GeoPoint?) {
        if (closed) return
        userLocation = point
        renderer.setUserLocation(point.takeIf { userLocationEnabled })
    }

    override fun setUserLocationEnabled(enabled: Boolean) {
        if (closed) return
        userLocationEnabled = enabled
        renderer.setUserLocation(userLocation.takeIf { enabled })
    }

    override fun lockTarget(
        point: GeoPoint,
        zoom: Float?
    ) {
        if (closed) return
        lockedTarget = point
        lockedZoom = zoom
        replayLockedTarget()
    }

    override fun unlockTarget() {
        lockedTarget = null
        lockedZoom = null
    }

    override fun snapshotScene(): MapController.SceneSnapshot =
        MapController.SceneSnapshot(
            cameraPosition = _cameraPosition.value,
            markers = pendingMarkers,
            routes = pendingRoutes,
            circles = pendingCircles,
            padding = pendingPadding,
            lockedTarget = lockedTarget,
            lockedZoom = lockedZoom
        )

    override fun close() {
        if (closed) return
        closed = true
        renderer.setListener(null)
        renderer.close()
        scope.cancel()
    }

    private fun replayLockedTarget() {
        val target = lockedTarget ?: return
        val zoom = lockedZoom ?: _cameraPosition.value.zoom
        scope.launch {
            renderer.animateTo(target, zoom, 250)
        }
    }

    private fun emitCamera(
        target: GeoPoint,
        zoom: Float,
        bearing: Float,
        tilt: Float
    ) {
        val next = CameraPosition(target, zoom, bearing, tilt, pendingPadding)
        val prev = lastEmittedCamera
        if (prev != null && cameraEpsilonEqual(prev, next)) return
        lastEmittedCamera = next
        _cameraPosition.value = next
    }

    private fun cameraEpsilonEqual(
        a: CameraPosition,
        b: CameraPosition
    ): Boolean {
        return kotlin.math.abs(a.target.lat - b.target.lat) < 1e-6 &&
            kotlin.math.abs(a.target.lng - b.target.lng) < 1e-6 &&
            kotlin.math.abs(a.zoom - b.zoom) < 1e-3 &&
            kotlin.math.abs(a.bearing - b.bearing) < 0.1f &&
            kotlin.math.abs(a.tilt - b.tilt) < 0.1f
    }
}
