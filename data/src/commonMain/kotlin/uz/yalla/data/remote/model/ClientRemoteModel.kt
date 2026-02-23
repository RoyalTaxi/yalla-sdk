package uz.yalla.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ClientRemoteModel(
    val phone: String?,
    val given_names: String?,
    val sur_name: String?,
    val image: String?,
    val birthday: String?,
    val balance: Long?,
    val gender: String?
)
