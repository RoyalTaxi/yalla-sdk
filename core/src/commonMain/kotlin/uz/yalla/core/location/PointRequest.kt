package uz.yalla.core.location

import uz.yalla.core.geo.GeoPoint

public data class PointRequest(
    val kind: PointKind,
    val point: GeoPoint
)
