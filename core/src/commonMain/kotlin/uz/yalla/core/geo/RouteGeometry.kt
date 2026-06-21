package uz.yalla.core.geo

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

private const val METERS_PER_DEG_LAT = 111_320.0

/**
 * Returns the compass bearing (degrees clockwise from north, via [GeoPoint.bearingTo]) of the route
 * segment nearest to [point], or `null` if the route has fewer than two points or the nearest
 * segment is more than [maxOffRouteMeters] away. Used for the live driver heading arrow.
 */
public fun headingAlongRoute(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = 60.0
): Float? {
    if (route.size < 2) return null
    // Hoist the longitude-scale `cos()` out of the per-segment loop: the route's latitude span is
    // tiny, so one representative value (the query point's latitude) is used for every segment.
    // Behavior is unchanged versus the previous per-segment `cos(a.lat)`; cost drops O(segments)->O(1).
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
