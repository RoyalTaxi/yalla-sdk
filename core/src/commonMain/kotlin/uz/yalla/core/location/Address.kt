package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Basic address representation with geographic coordinates.
 *
 * Used as the standard address model throughout the order creation flow,
 * representing both pickup and destination points.
 *
 * @see AddressOption
 * @see SavedAddress
 */
@Serializable
data class Address(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("isFromDatabase") val isFromDatabase: Boolean
)
