package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

/**
 * Immutable state of the center-screen map marker used by [MapController][uz.yalla.maps.api.MapController].
 *
 * Tracks the geographic position, whether the camera is currently in motion,
 * and whether the motion was triggered by a user gesture.
 *
 * @property point Geographic coordinate of the marker.
 * @property isMoving `true` while the camera (and thus the marker) is moving.
 * @property isByUser `true` when the current movement was initiated by a user gesture.
 * @since 0.0.1
 */
@Immutable
data class MarkerState(
    val point: GeoPoint,
    val isMoving: Boolean,
    val isByUser: Boolean
) {
    companion object {
        /**
         * Default marker state at [GeoPoint.Zero], stationary, not user-initiated.
         *
         * @since 0.0.1
         */
        val INITIAL =
            MarkerState(
                point = GeoPoint.Zero,
                isMoving = false,
                isByUser = false
            )
    }
}
