package uz.yalla.core.geo

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

public fun headingAlongRoute(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = 60.0
): Float? {
    if (route.size < 2) return null
    var bestDistanceMeters = Double.MAX_VALUE
    var bestBearing: Double? = null
    for (i in 0 until route.lastIndex) {
        val a = route[i]
        val b = route[i + 1]
        if (a == b) continue
        val distance = point.distanceToSegmentMeters(a, b)
        if (distance < bestDistanceMeters) {
            bestDistanceMeters = distance
            bestBearing = a.bearingTo(b)
        }
    }
    if (bestDistanceMeters > maxOffRouteMeters) return null
    return bestBearing?.toFloat()
}

private fun GeoPoint.distanceToSegmentMeters(a: GeoPoint, b: GeoPoint): Double {
    val metersPerDegLat = 111_320.0
    val metersPerDegLng = 111_320.0 * cos(a.lat * PI / 180.0)
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
