package uz.yalla.core.geo

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

private const val METERS_PER_DEG_LAT = 111_320.0
private const val DEFAULT_ROUTE_MAGNET_METERS = 45.0
private const val DEFAULT_ROUTE_CLEAR_METERS = 25.0

public enum class RouteProgressKind {
    NO_ROUTE,
    ON_ROUTE,
    OFF_ROUTE,
    PASSED_ROUTE
}

public data class RouteProgress(
    val kind: RouteProgressKind,
    val displayPoint: GeoPoint,
    val remainingRoute: List<GeoPoint>,
    val heading: Float?
)

public fun headingAlongRoute(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = DEFAULT_ROUTE_MAGNET_METERS
): Float? {
    val projection = nearestRouteProjection(point, route) ?: return null
    if (projection.distanceMeters > maxOffRouteMeters) return null
    return projection.bearing.toFloat()
}

internal fun snappedPointOnRoute(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = DEFAULT_ROUTE_MAGNET_METERS
): GeoPoint? {
    val projection = nearestRouteProjection(point, route) ?: return null
    if (projection.distanceMeters > maxOffRouteMeters) return null
    return projection.point
}

internal fun isPointNearRoute(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = DEFAULT_ROUTE_MAGNET_METERS
): Boolean {
    val projection = nearestRouteProjection(point, route) ?: return false
    return projection.distanceMeters <= maxOffRouteMeters
}

internal fun remainingRouteAfter(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = DEFAULT_ROUTE_MAGNET_METERS,
    clearWithinMeters: Double = DEFAULT_ROUTE_CLEAR_METERS
): List<GeoPoint> {
    return routeProgress(point, route, maxOffRouteMeters, clearWithinMeters).remainingRoute
}

public fun routeProgress(
    point: GeoPoint,
    route: List<GeoPoint>,
    maxOffRouteMeters: Double = DEFAULT_ROUTE_MAGNET_METERS,
    clearWithinMeters: Double = DEFAULT_ROUTE_CLEAR_METERS
): RouteProgress {
    val projection =
        nearestRouteProjection(point, route)
            ?: return RouteProgress(
                kind = RouteProgressKind.NO_ROUTE,
                displayPoint = point,
                remainingRoute = route,
                heading = null
            )
    val isPastEnd =
        projection.segmentIndex == route.lastIndex - 1 &&
            projection.unclampedT >= 1.0 &&
            projection.crossTrackMeters <= maxOffRouteMeters
    val isOnRoute = projection.distanceMeters <= maxOffRouteMeters
    val heading = projection.bearing.toFloat()
    if (!isOnRoute && !isPastEnd) {
        return RouteProgress(
            kind = RouteProgressKind.OFF_ROUTE,
            displayPoint = point,
            remainingRoute = route,
            heading = null
        )
    }
    if (isPastEnd || projection.point.distanceTo(route.last()) <= clearWithinMeters) {
        return RouteProgress(
            kind = RouteProgressKind.PASSED_ROUTE,
            displayPoint = projection.point,
            remainingRoute = emptyList(),
            heading = heading
        )
    }
    val remaining =
        buildList {
            add(projection.point)
            addAll(route.drop(projection.segmentIndex + 1))
        }.withoutConsecutiveDuplicates()
    return RouteProgress(
        kind = RouteProgressKind.ON_ROUTE,
        displayPoint = projection.point,
        remainingRoute = remaining,
        heading = heading
    )
}

private data class RouteProjection(
    val point: GeoPoint,
    val segmentIndex: Int,
    val distanceMeters: Double,
    val crossTrackMeters: Double,
    val unclampedT: Double,
    val bearing: Double
)

private fun nearestRouteProjection(
    point: GeoPoint,
    route: List<GeoPoint>
): RouteProjection? {
    if (route.size < 2) return null
    val metersPerDegLng = METERS_PER_DEG_LAT * cos(point.lat * PI / 180.0)
    var best: RouteProjection? = null
    for (i in 0 until route.lastIndex) {
        val a = route[i]
        val b = route[i + 1]
        if (a == b) continue
        val projection = point.projectToSegment(a, b, i, metersPerDegLng)
        if (best == null || projection.distanceMeters < best.distanceMeters) best = projection
    }
    return best
}

private fun GeoPoint.projectToSegment(
    a: GeoPoint,
    b: GeoPoint,
    segmentIndex: Int,
    metersPerDegLng: Double
): RouteProjection {
    val metersPerDegLat = METERS_PER_DEG_LAT
    val px = (lng - a.lng) * metersPerDegLng
    val py = (lat - a.lat) * metersPerDegLat
    val bx = (b.lng - a.lng) * metersPerDegLng
    val by = (b.lat - a.lat) * metersPerDegLat
    val segLenSq = bx * bx + by * by
    if (segLenSq == 0.0) {
        val distance = sqrt(px * px + py * py)
        return RouteProjection(a, segmentIndex, distance, distance, 0.0, a.bearingTo(b))
    }
    val unclampedT = (px * bx + py * by) / segLenSq
    val t = unclampedT.coerceIn(0.0, 1.0)
    val dx = px - bx * t
    val dy = py - by * t
    val projected =
        GeoPoint(
            lat = a.lat + (b.lat - a.lat) * t,
            lng = a.lng + (b.lng - a.lng) * t
        )
    val crossTrackMeters = kotlin.math.abs(px * by - py * bx) / sqrt(segLenSq)
    return RouteProjection(
        point = projected,
        segmentIndex = segmentIndex,
        distanceMeters = sqrt(dx * dx + dy * dy),
        crossTrackMeters = crossTrackMeters,
        unclampedT = unclampedT,
        bearing = a.bearingTo(b)
    )
}

private fun List<GeoPoint>.withoutConsecutiveDuplicates(): List<GeoPoint> =
    buildList {
        for (point in this@withoutConsecutiveDuplicates) {
            if (lastOrNull() != point) add(point)
        }
    }
