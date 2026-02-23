package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

@Immutable
data class MarkerState(
    val point: GeoPoint,
    val isMoving: Boolean,
    val isByUser: Boolean
) {
    companion object {
        val INITIAL =
            MarkerState(
                point = GeoPoint.Zero,
                isMoving = false,
                isByUser = false
            )
    }
}
