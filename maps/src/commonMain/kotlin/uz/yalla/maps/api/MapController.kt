package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.config.MapConstants

interface MapController {
    val cameraPosition: StateFlow<CameraPosition>
    val markerState: StateFlow<MarkerState>
    val isReady: StateFlow<Boolean>

    // Camera movement
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

    suspend fun fitBounds(
        points: List<GeoPoint>,
        padding: PaddingValues = PaddingValues(),
        animate: Boolean = true
    )

    // Zoom controls
    suspend fun zoomIn()

    suspend fun zoomOut()

    suspend fun setZoom(zoom: Float)

    // Padding
    fun setDesiredPadding(padding: PaddingValues)

    suspend fun updatePadding(padding: PaddingValues)

    // Marker state
    fun updateMarkerState(state: MarkerState)

    fun setMarkerPosition(point: GeoPoint)

    fun clearMarker()

    // Lifecycle
    fun onMapReady()

    fun reset()

    fun close() {}

    companion object {
        const val ANIMATION_DURATION = 1000
    }
}
