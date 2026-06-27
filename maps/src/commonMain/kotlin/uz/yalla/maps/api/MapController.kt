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

    /**
     * The live platform host backing this controller, or `null` before a backend exists.
     *
     * A switching controller re-emits the active backend's host whenever the provider changes.
     * This is how [MapView] reaches the platform-rendered surface — replacing the render-time
     * `controller as AndroidMapController` / `as IosMapController` casts that this property obsoletes.
     */
    public val platformHost: StateFlow<PlatformMapHost?>

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

    /**
     * Declares which rendered route each marker follows, as a `markerId -> routeId` map.
     *
     * This is the renderer-level binding channel for route-following, orthogonal to the pure
     * [MapMarker] value type: a flat (driver) marker whose id appears here is glued to the
     * referenced [MapRoute]'s arc-length so the car tracks the drawn polyline through corners.
     * A marker absent from the map (or mapped to a routeId with no matching route) free-roams.
     *
     * Bindings take precedence over the deprecated [MapMarker.followsRouteId] field; the field
     * remains a fallback so existing callers keep working until they migrate to this channel.
     * Passing an empty map clears all bindings.
     */
    public fun setRouteBindings(bindings: Map<String, String>)

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
