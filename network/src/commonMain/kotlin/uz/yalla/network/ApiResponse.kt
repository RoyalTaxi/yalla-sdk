package uz.yalla.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val result: T? = null
)
