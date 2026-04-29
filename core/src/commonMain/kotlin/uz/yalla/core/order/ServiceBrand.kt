package uz.yalla.core.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceBrand(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("photo") val photo: String
)
