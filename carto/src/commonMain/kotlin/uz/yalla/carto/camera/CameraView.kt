package uz.yalla.carto.camera

import androidx.compose.foundation.layout.PaddingValues
import uz.yalla.core.geo.GeoPoint

public data class CameraView(
    val target: GeoPoint,
    val zoom: Float,
    val bearing: Float = 0f,
    val tilt: Float = 0f,
    val padding: PaddingValues = PaddingValues()
) {
    public companion object {
        public const val DEFAULT_ZOOM: Float = 15f
        public const val ZOOM_MIN: Float = 4f
        public const val ZOOM_MAX: Float = 21f
        public const val FIT_ZOOM_MAX: Float = 17f

        public val DEFAULT: CameraView = CameraView(GeoPoint.Zero, DEFAULT_ZOOM)
    }
}
