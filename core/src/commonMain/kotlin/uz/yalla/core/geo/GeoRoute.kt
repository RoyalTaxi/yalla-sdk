package uz.yalla.core.geo

import kotlinx.serialization.Serializable

/**
 * Represents a geographic route between two or more points.
 *
 * @property points Ordered list of coordinates forming the route
 * @property distance Total route distance in meters
 * @property duration Estimated travel duration in seconds
 */
@Serializable
data class GeoRoute(
    val points: List<GeoPoint>,
    val distance: Double,
    val duration: Double
) {
    val bounds: GeoBounds?
        get() = GeoBounds.fromPoints(points)

    companion object {
        val Empty =
            GeoRoute(
                points = emptyList(),
                distance = 0.0,
                duration = 0.0
            )
    }
}
