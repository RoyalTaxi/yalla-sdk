package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

@Immutable
data class CenterPinState(
    val point: GeoPoint,
    val isMoving: Boolean,
    val isByUser: Boolean
) {
    companion object {
        val INITIAL = CenterPinState(
            point = GeoPoint.Zero,
            isMoving = false,
            isByUser = false
        )
    }
}
