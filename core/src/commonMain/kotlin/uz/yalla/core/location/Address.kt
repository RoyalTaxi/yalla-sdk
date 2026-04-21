package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Basic address representation with geographic coordinates.
 *
 * Used as the standard address model throughout the order creation flow,
 * representing both pickup and destination points.
 *
 * @property id Database identifier, `null` for addresses not yet persisted
 * @property name Human-readable address string
 * @property lat Latitude in degrees
 * @property lng Longitude in degrees
 * @property isFromDatabase `true` if this address was loaded from local history
 * @see AddressOption
 * @see SavedAddress
 * @since 0.0.1
 */
@Serializable
data class Address(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("isFromDatabase") val isFromDatabase: Boolean
)
