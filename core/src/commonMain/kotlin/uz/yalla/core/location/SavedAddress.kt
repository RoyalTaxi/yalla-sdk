package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
