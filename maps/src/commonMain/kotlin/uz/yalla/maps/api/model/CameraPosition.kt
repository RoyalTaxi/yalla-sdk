package uz.yalla.maps.api.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint

@Immutable
data class CameraPosition(
    val target: GeoPoint,
    val zoom: Float,
    val bearing: Float = 0f,
    val tilt: Float = 0f,
    val padding: PaddingValues = PaddingValues()
) {
    companion object {
        val DEFAULT =
            CameraPosition(
                target = GeoPoint.Zero,
                zoom = 15f
            )
    }
}
