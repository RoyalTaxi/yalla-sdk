package uz.yalla.maps.motion

import uz.yalla.core.geo.GeoPoint

public data class Pose(
    val point: GeoPoint,
    val bearing: Float
)
