package uz.yalla.carto.camera

import androidx.compose.foundation.layout.PaddingValues
import uz.yalla.core.geo.GeoPoint

public data class CameraIntent(
    val target: GeoPoint? = null,
    val zoom: Float? = null,
    val bounds: List<GeoPoint>? = null,
    val boundsPadding: PaddingValues = PaddingValues(),
    val animate: Boolean = true,
    val durationMs: Int = 1000
)
