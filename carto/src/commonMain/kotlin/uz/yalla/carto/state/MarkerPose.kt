package uz.yalla.carto.state

import uz.yalla.carto.model.MapAnchor
import uz.yalla.core.geo.GeoPoint

public data class MarkerPose(
    val id: String,
    val point: GeoPoint,
    val bearing: Float,
    val iconKey: String?,
    val anchor: MapAnchor
)
