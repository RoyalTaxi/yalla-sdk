package uz.yalla.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorBody(
    val message: String? = null
)
