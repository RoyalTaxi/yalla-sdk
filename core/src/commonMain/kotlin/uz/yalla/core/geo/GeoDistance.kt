package uz.yalla.core.geo

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6_371_000.0

public fun GeoPoint.distanceTo(other: GeoPoint): Double {
    val latDelta = (other.lat - lat).toRadians()
    val lngDelta = (other.lng - lng).toRadians()
    val a =
        sin(latDelta / 2) * sin(latDelta / 2) +
            cos(lat.toRadians()) * cos(other.lat.toRadians()) * sin(lngDelta / 2) * sin(lngDelta / 2)
    return 2 * EARTH_RADIUS_METERS * asin(sqrt(a))
}

public fun GeoPoint.bearingTo(other: GeoPoint): Double {
    val lat1 = lat.toRadians()
    val lat2 = other.lat.toRadians()
    val lngDelta = (other.lng - lng).toRadians()
    val y = sin(lngDelta) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lngDelta)
    val degrees = atan2(y, x) * 180.0 / kotlin.math.PI
    return (degrees + 360.0) % 360.0
}

private fun Double.toRadians(): Double = this * kotlin.math.PI / 180.0
