package uz.yalla.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseWrapper<T>(
    val result: T? = null
)
