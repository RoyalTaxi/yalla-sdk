package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A user-saved place with pre-computed routing metrics from that place to the
 * last known position.
 *
 * [distance] is in metres and [duration] in seconds, both computed server-side
 * at the time the address was fetched and reused as a cheap estimate before any
 * fresh route lookup. [parent] holds the district or city the address belongs
 * to (`null` when the geocoder couldn't classify it).
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
    @Serializable
    data class Parent(
        @SerialName("name") val name: String?
    )
}
