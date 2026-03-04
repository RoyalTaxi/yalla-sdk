package uz.yalla.core.location

/**
 * A calculated route between waypoints.
 *
 * @property distance Total route distance in meters
 * @property duration Estimated travel time in seconds
 * @property points Ordered list of coordinates forming the route polyline
 * @since 0.0.1
 */
data class Route(
    val distance: Double,
    val duration: Double,
    val points: List<Point>
) {
    /** A single coordinate in the route polyline. */
    data class Point(
        val lat: Double,
        val lng: Double
    )
}
