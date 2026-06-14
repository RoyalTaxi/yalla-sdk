package uz.yalla.network

import kotlinx.serialization.Serializable

@Serializable
public data class ApiErrorResponse(
    val message: String? = null
)
