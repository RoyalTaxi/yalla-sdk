package uz.yalla.core.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A taxi service tier offered on the platform (economy, comfort, business, etc.).
 *
 * Used in tariff selection so the rider can pick a price class before creating an
 * order. [photo] is a CDN URL for the brand icon shown in the picker.
 */
@Serializable
data class ServiceBrand(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("photo") val photo: String
)
