package uz.yalla.core.geo

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6_371_000.0

public fun GeoPoint.distanceTo(other: GeoPoint): Double {
    val latDelta = (other.lat - lat).toRadians()
    val lngDelta = (other.lng - lng).toRadians()
    val a = sin(latDelta / 2) * sin(latDelta / 2) +
        cos(lat.toRadians()) * cos(other.lat.toRadians()) * sin(lngDelta / 2) * sin(lngDelta / 2)
    return 2 * EARTH_RADIUS_METERS * asin(sqrt(a))
}

private fun Double.toRadians(): Double = this * kotlin.math.PI / 180.0
