package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A calculated route between waypoints, returned by the routing API.
 *
 * Contains the total distance, estimated duration, and a polyline
 * represented as an ordered list of [Point] coordinates for map rendering.
 *
 * @property distance Total route distance in meters
 * @property duration Estimated travel time in seconds
 * @property points Ordered list of coordinates forming the route polyline
 * @see PointRequest
 * @since 0.0.1
 */
@Serializable
data class Route(
    @SerialName("distance") val distance: Double,
    @SerialName("duration") val duration: Double,
    @SerialName("points") val points: List<Point>
) {
    /**
     * A single coordinate in the route polyline.
     *
     * @property lat Latitude in degrees
     * @property lng Longitude in degrees
     * @since 0.0.1
     */
    @Serializable
    data class Point(
        @SerialName("lat") val lat: Double,
        @SerialName("lng") val lng: Double
    )
}
