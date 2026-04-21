package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
@Serializable
data class PointRequest(
    @SerialName("kind") val kind: PointKind,
    @SerialName("lng") val lng: Double,
    @SerialName("lat") val lat: Double
)
