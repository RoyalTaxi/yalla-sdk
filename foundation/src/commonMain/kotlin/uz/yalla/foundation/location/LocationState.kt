package uz.yalla.foundation.location

import uz.yalla.core.geo.GeoPoint

internal data class ExtendedLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val timestamp: Long = 0L
) {
    fun toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
}

enum class LocationPermissionState {
    NOT_DETERMINED,
    GRANTED,
    DENIED,

    /** User selected "Don't ask again". */
    DENIED_PERMANENTLY
}
