package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.CenterPinState
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapEvent
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute
import uz.yalla.maps.api.model.MapStyle
import uz.yalla.maps.config.MapConstants

interface MapController {

    val cameraPosition: StateFlow<CameraPosition>

    val centerPin: StateFlow<CenterPinState>

    val isReady: StateFlow<Boolean>

    val events: SharedFlow<MapEvent>

    suspend fun moveTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat()
    )

    suspend fun animateTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    suspend fun fitBounds(points: List<GeoPoint>, animate: Boolean = true, padding: PaddingValues? = null)

    suspend fun zoomIn()

    suspend fun zoomOut()

    suspend fun setZoom(zoom: Float)

    suspend fun setStyle(style: MapStyle, isDark: Boolean)

    fun setDesiredPadding(padding: PaddingValues)

    fun setInteractionEnabled(enabled: Boolean)

    fun setMarkers(markers: List<MapMarker>)

    fun setRoutes(routes: List<MapRoute>)

    fun setCircles(circles: List<MapCircle>)

    fun setUserLocation(point: GeoPoint?)

    fun lockTarget(point: GeoPoint, zoom: Float? = null)

    fun unlockTarget()

    fun snapshotScene(): SceneSnapshot

    fun close()

    data class SceneSnapshot(
        val cameraPosition: CameraPosition,
        val markers: List<MapMarker>,
        val routes: List<MapRoute>,
        val circles: List<MapCircle>,
        val padding: PaddingValues,
        val lockedTarget: GeoPoint?,
        val lockedZoom: Float?
    )

    companion object {
        const val ANIMATION_DURATION = 1000
    }
}
