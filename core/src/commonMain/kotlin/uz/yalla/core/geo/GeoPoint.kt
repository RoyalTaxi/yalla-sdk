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
 * Instances are validated on construction — latitude must be in [-90, 90]
 * and longitude in [-180, 180], or an [IllegalArgumentException] is thrown.
 *
 * ## Usage
 * ```kotlin
 * val tashkent = GeoPoint(lat = 41.2995, lng = 69.2401)
 * val samarkand = GeoPoint(lat = 39.6542, lng = 66.9597)
 * val distance = tashkent.distanceTo(samarkand) // ~263 km
 * ```
 *
 * @property lat Latitude in degrees, must be in [-90, 90]
 * @property lng Longitude in degrees, must be in [-180, 180]
 * @see uz.yalla.core.contract.location.LocationProvider
 * @since 0.0.1
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
        /**
         * A [GeoPoint] at the origin (0, 0) in the Gulf of Guinea.
         *
         * Used as a default/fallback value when no real position is available.
         */
        val Zero = GeoPoint(0.0, 0.0)
    }

    /**
     * Calculates the great-circle distance to [other] using the Haversine formula.
     *
     * The result is accurate for any two points on Earth, including antipodal points.
     *
     * @param other The target geographic point
     * @return Distance in meters
     */
    fun distanceTo(other: GeoPoint): Double {
        val dLat = (other.lat - lat).toRadians()
        val dLng = (other.lng - lng).toRadians()
        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                cos(lat.toRadians()) * cos(other.lat.toRadians()) *
                sin(dLng / 2) * sin(dLng / 2)
        val clampedA = a.coerceIn(0.0, 1.0)
        val c = 2 * atan2(sqrt(clampedA), sqrt(1 - clampedA))
        return EARTH_RADIUS_METERS * c
    }
}
