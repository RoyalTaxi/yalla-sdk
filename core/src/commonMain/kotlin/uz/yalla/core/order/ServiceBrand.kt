package uz.yalla.core.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Taxi service brand/company information.
 *
 * @property id Unique brand identifier
 * @property name Display name of the service
 * @property photo URL of the brand logo
 * @since 0.0.1
 */
@Serializable
data class ServiceBrand(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("photo") val photo: String
)
