package uz.yalla.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class BrandServiceRemoteModel(
    val id: Int?,
    val name: String?,
    val photo: String?
)
