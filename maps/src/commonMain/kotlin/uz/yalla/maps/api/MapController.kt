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

public interface MapController {
    public val cameraPosition: StateFlow<CameraPosition>

    public val centerPin: StateFlow<CenterPinState>

    public val isReady: StateFlow<Boolean>

    public val events: SharedFlow<MapEvent>

    public suspend fun moveTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat()
    )

    public suspend fun animateTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    public suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    public suspend fun fitBounds(
        points: List<GeoPoint>,
        animate: Boolean = true,
        padding: PaddingValues? = null
    )

    public suspend fun zoomIn()

    public suspend fun zoomOut()

    public suspend fun setZoom(zoom: Float)

    public suspend fun setStyle(
        style: MapStyle,
        isDark: Boolean
    )

    public fun setDesiredPadding(padding: PaddingValues)

    public fun setInteractionEnabled(enabled: Boolean)

    public fun setMarkers(markers: List<MapMarker>)

    public fun setRoutes(routes: List<MapRoute>)

    public fun setCircles(circles: List<MapCircle>)

    public fun setUserLocation(point: GeoPoint?)

    public fun setUserLocationEnabled(enabled: Boolean)

    public fun lockTarget(
        point: GeoPoint,
        zoom: Float? = null
    )

    public fun unlockTarget()

    public fun close()

    public companion object {
        public const val ANIMATION_DURATION: Int = 1000
    }
}
