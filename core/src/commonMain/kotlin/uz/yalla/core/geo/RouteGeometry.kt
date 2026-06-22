package uz.yalla.core.geo

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

private const val METERS_PER_DEG_LAT = 111_320.0

public fun headingAlongRoute(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = 60.0
): Float? {
    if (route.size < 2) return null
    val metersPerDegLng = METERS_PER_DEG_LAT * cos(point.lat * PI / 180.0)
    var bestDistanceMeters = Double.MAX_VALUE
    var bestBearing: Double? = null
    for (i in 0 until route.lastIndex) {
        val a = route[i]
        val b = route[i + 1]
        if (a == b) continue
        val distance = point.distanceToSegmentMeters(a, b, metersPerDegLng)
        if (distance < bestDistanceMeters) {
            bestDistanceMeters = distance
            bestBearing = a.bearingTo(b)
        }
    }
    if (bestDistanceMeters > maxOffRouteMeters) return null
    return bestBearing?.toFloat()
}

private fun GeoPoint.distanceToSegmentMeters(
    a: GeoPoint,
    b: GeoPoint,
    metersPerDegLng: Double
): Double {
    val metersPerDegLat = METERS_PER_DEG_LAT
    val px = (lng - a.lng) * metersPerDegLng
    val py = (lat - a.lat) * metersPerDegLat
    val bx = (b.lng - a.lng) * metersPerDegLng
    val by = (b.lat - a.lat) * metersPerDegLat
    val segLenSq = bx * bx + by * by
    if (segLenSq == 0.0) return sqrt(px * px + py * py)
    val t = ((px * bx + py * by) / segLenSq).coerceIn(0.0, 1.0)
    val dx = px - bx * t
    val dy = py - by * t
    return sqrt(dx * dx + dy * dy)
}
