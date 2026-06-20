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

/**
 * The cross-platform map contract buyers compile against. A single backend (Google or MapLibre)
 * implements rendering; [SwitchingMapController] composes two backends behind this same surface.
 * State is exposed as flows ([cameraPosition], [centerPin], [isReady], [events]); imperative
 * commands ([moveTo], [setMarkers], …) mutate the live map. Call [close] to release the backend.
 */
public interface MapController {
    /** The current camera (target, zoom, bearing, tilt, padding). Updates as the map moves. */
    public val cameraPosition: StateFlow<CameraPosition>

    /** The center-pin state: where the screen center maps to, whether it is moving and by whom. */
    public val centerPin: StateFlow<CenterPinState>

    /** True once the map surface and style are loaded and ready for commands. */
    public val isReady: StateFlow<Boolean>

    /** One-shot interaction/lifecycle signals (taps, long-press, provider failure). */
    public val events: SharedFlow<MapEvent>

    /** Jumps the camera to [point] at [zoom] without animation. */
    public suspend fun moveTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat()
    )

    /** Animates the camera to [point] at [zoom] over [durationMs]. */
    public suspend fun animateTo(
        point: GeoPoint,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    /** Animates the camera to [point] with the given [bearing] at [zoom] over [durationMs]. */
    public suspend fun animateToWithBearing(
        point: GeoPoint,
        bearing: Float,
        zoom: Float = MapConstants.DEFAULT_ZOOM.toFloat(),
        durationMs: Int = ANIMATION_DURATION
    )

    /** Frames the camera so all [points] are visible, respecting [padding]; animated when [animate]. */
    public suspend fun fitBounds(
        points: List<GeoPoint>,
        animate: Boolean = true,
        padding: PaddingValues? = null
    )

    /** Zooms in by one step. */
    public suspend fun zoomIn()

    /** Zooms out by one step. */
    public suspend fun zoomOut()

    /** Sets the absolute zoom level. */
    public suspend fun setZoom(zoom: Float)

    /** Applies the map [style], selecting its light/dark variant per [isDark]. */
    public suspend fun setStyle(
        style: MapStyle,
        isDark: Boolean
    )

    /** Sets the edge padding the camera/center-pin should respect (e.g. for overlay UI). */
    public fun setDesiredPadding(padding: PaddingValues)

    /** Enables or disables user gestures (pan/zoom/rotate). */
    public fun setInteractionEnabled(enabled: Boolean)

    /** Replaces the rendered markers, diffed by [MapMarker.id]. */
    public fun setMarkers(markers: List<MapMarker>)

    /** Replaces the rendered routes/polylines. */
    public fun setRoutes(routes: List<MapRoute>)

    /** Replaces the rendered circles. */
    public fun setCircles(circles: List<MapCircle>)

    /** Sets the user-location dot position, or null to hide it. */
    public fun setUserLocation(point: GeoPoint?)

    /** Toggles user-location rendering without losing the last known [setUserLocation] point. */
    public fun setUserLocationEnabled(enabled: Boolean)

    /** Locks the camera target to [point] (optionally at [zoom]); re-applied on padding changes. */
    public fun lockTarget(
        point: GeoPoint,
        zoom: Float? = null
    )

    /** Releases a [lockTarget]. */
    public fun unlockTarget()

    /**
     * Captures the current scene so it can be replayed onto another backend. Used by
     * [SwitchingMapController] to hand the scene over during a provider switch.
     *
     * TODO(quality, needs-decision): finding #5 — this scene-handover seam (and the
     * lockTarget/unlockTarget pair it replays) leaks SwitchingMapController's private mechanism
     * onto the contract every backend must implement. It should be an internal seam, but
     * snapshotScene/SceneSnapshot are frozen in the committed `.api`/`.klib.api` dumps, so demoting
     * them is a breaking dump change. Needs the owner's sign-off on a binary-API break.
     */
    public fun snapshotScene(): SceneSnapshot

    /** Releases the backend and all its resources. The controller must not be used afterwards. */
    public fun close()

    /** A captured map scene used to hand state from one backend to another across a switch. */
    public data class SceneSnapshot(
        val cameraPosition: CameraPosition,
        val markers: List<MapMarker>,
        val routes: List<MapRoute>,
        val circles: List<MapCircle>,
        val padding: PaddingValues,
        val lockedTarget: GeoPoint?,
        val lockedZoom: Float?
    )

    public companion object {
        /** Default animation duration, in milliseconds, for [animateTo]/[animateToWithBearing]. */
        public const val ANIMATION_DURATION: Int = 1000
    }
}
