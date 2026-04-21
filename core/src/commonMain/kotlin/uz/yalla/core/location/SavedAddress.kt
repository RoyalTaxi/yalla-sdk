package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User's saved/favorite address with navigation metrics.
 *
 * @property distance Distance from current location in meters
 * @property duration Estimated travel time in seconds
 * @property lat Latitude
 * @property lng Longitude
 * @property address Full address string
 * @property title User-assigned label
 * @property kind Place category (home, work, other)
 * @property parent Parent location info (e.g., city name)
 * @since 0.0.1
 */
@Serializable
data class SavedAddress(
    @SerialName("distance") val distance: Double,
    @SerialName("duration") val duration: Double,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("address") val address: String,
    @SerialName("title") val title: String,
    @SerialName("kind") val kind: PlaceKind,
    @SerialName("parent") val parent: Parent
) {
    /**
     * Parent location for hierarchical address display (e.g., city or district name).
     *
     * @property name Parent location name, or `null` if not available
     * @since 0.0.1
     */
    @Serializable
    data class Parent(
        @SerialName("name") val name: String?
    )
}
