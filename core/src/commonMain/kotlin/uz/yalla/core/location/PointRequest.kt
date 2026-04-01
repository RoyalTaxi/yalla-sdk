package uz.yalla.core.location

/**
 * A waypoint for route calculation requests.
 *
 * Sent to the routing API to compute distance, duration, and polyline
 * between multiple points.
 *
 * @property kind Role of this point in the route (start, intermediate, stop)
 * @property lng Longitude in degrees
 * @property lat Latitude in degrees
 * @see PointKind
 * @see Route
 * @since 0.0.1
 */
data class PointRequest(
    val kind: PointKind,
    val lng: Double,
    val lat: Double
)
