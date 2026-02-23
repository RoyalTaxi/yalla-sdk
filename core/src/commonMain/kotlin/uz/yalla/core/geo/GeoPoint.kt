package uz.yalla.core.geo

import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_METERS = 6371000.0

private fun Double.toRadians(): Double = this * (PI / 180.0)

/**
 * Represents a geographic coordinate with latitude and longitude.
 *
 * @property lat Latitude in degrees, must be in [-90, 90]
 * @property lng Longitude in degrees, must be in [-180, 180]
 */
@Serializable
data class GeoPoint(
    val lat: Double,
    val lng: Double
) {
    init {
        require(lat in -90.0..90.0) { "Latitude must be in [-90, 90], was $lat" }
        require(lng in -180.0..180.0) { "Longitude must be in [-180, 180], was $lng" }
    }

    companion object {
        val Zero = GeoPoint(0.0, 0.0)
    }

    /** Calculates the Haversine distance to [other] point in meters. */
    fun distanceTo(other: GeoPoint): Double {
        val dLat = (other.lat - lat).toRadians()
        val dLng = (other.lng - lng).toRadians()
        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                cos(lat.toRadians()) * cos(other.lat.toRadians()) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }
}
