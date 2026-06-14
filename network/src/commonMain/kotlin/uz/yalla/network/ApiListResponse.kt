package uz.yalla.network

import kotlinx.serialization.Serializable

@Serializable
public data class ApiListResponse<T>(
    val list: List<T>? = null
)
