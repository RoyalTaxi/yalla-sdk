package uz.yalla.maps.api

import androidx.compose.foundation.layout.PaddingValues
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.CenterPinState
import uz.yalla.maps.config.MapConstants

interface MapController {
    val cameraPosition: StateFlow<CameraPosition>

    val centerPin: StateFlow<CenterPinState>

    val isReady: StateFlow<Boolean>

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

    suspend fun zoomIn()

    suspend fun zoomOut()

    suspend fun setZoom(zoom: Float)

    fun setDesiredPadding(padding: PaddingValues)

    suspend fun updatePadding(padding: PaddingValues)

    fun updateCenterPin(state: CenterPinState)

    fun setCenterPin(point: GeoPoint)

    fun clearCenterPin()

    fun onMapReady()

    fun reset()

    fun close() {}

    companion object {
        const val ANIMATION_DURATION = 1000
    }
}
