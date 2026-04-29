package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A waypoint for route calculation requests.
 *
 * Sent to the routing API to compute distance, duration, and polyline
 * between multiple points.
 *
 * @see PointKind
 * @see Route
 */
@Serializable
data class PointRequest(
    @SerialName("kind") val kind: PointKind,
    @SerialName("lng") val lng: Double,
    @SerialName("lat") val lat: Double
)
