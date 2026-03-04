package uz.yalla.core.location

/**
 * A waypoint for route calculation requests.
 *
 * @property kind Role of this point in the route (start, intermediate, stop)
 * @property lng Longitude
 * @property lat Latitude
 * @since 0.0.1
 */
data class PointRequest(
    val kind: PointKind,
    val lng: Double,
    val lat: Double
)
