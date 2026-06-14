package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

@Immutable
public data class CenterPinState(
    val point: GeoPoint,
    val isMoving: Boolean,
    val isByUser: Boolean
) {
    public companion object {
        public val INITIAL: CenterPinState = CenterPinState(
            point = GeoPoint.Zero,
            isMoving = false,
            isByUser = false
        )
    }
}
