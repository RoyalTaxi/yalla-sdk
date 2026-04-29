package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.yalla.core.identity.AddressOptionId

/**
 * Address search result or autocomplete suggestion.
 *
 * Returned by address search APIs, includes distance from current location
 * for sorting by relevance. Displayed in the search results list with
 * [title] as the primary line and [address] as the secondary line.
 *
 * @property isFromDatabase `true` if from local search history rather than the search API
 * @see Address
 */
@Serializable
data class AddressOption(
    @SerialName("id") val id: AddressOptionId,
    @SerialName("title") val title: String,
    @SerialName("address") val address: String,
    @SerialName("distance") val distance: Double,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("isFromDatabase") val isFromDatabase: Boolean
)
