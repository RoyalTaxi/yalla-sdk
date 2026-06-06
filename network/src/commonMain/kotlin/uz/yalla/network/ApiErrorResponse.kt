package uz.yalla.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val message: String? = null
)
