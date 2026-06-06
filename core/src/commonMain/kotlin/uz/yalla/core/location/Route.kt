package uz.yalla.core.location

import uz.yalla.core.geo.GeoPoint

data class Route(
    val distance: Double,
    val duration: Double,
    val points: List<GeoPoint>
)
