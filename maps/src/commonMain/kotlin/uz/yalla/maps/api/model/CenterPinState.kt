package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

/**
 * Where the screen center currently maps to and whether the map is being moved.
 *
 * @param point the geographic point under the screen center.
 * @param isMoving true while the camera is animating or being dragged.
 * @param isByUser true when the current move was initiated by a user gesture (vs a programmatic command).
 */
@Immutable
public data class CenterPinState(
    val point: GeoPoint,
    val isMoving: Boolean,
    val isByUser: Boolean
) {
    public companion object {
        /** The pre-fix sentinel: center at [GeoPoint.Zero], not moving. */
        public val INITIAL: CenterPinState =
            CenterPinState(
                point = GeoPoint.Zero,
                isMoving = false,
                isByUser = false
            )
    }
}
