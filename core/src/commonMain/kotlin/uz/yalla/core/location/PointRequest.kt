package uz.yalla.core.location

import uz.yalla.core.geo.GeoPoint

data class PointRequest(
    val kind: PointKind,
    val point: GeoPoint
)
