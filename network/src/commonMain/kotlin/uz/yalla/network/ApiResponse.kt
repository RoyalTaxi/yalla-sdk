package uz.yalla.network

import kotlinx.serialization.Serializable

@Serializable
public data class ApiResponse<T>(
    val result: T? = null
)
