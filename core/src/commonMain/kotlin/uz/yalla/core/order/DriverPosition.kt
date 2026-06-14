package uz.yalla.core.order

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.identity.DriverId

public data class DriverPosition(
    val id: DriverId,
    val point: GeoPoint,
    val heading: Double,
    val distance: Double
)
