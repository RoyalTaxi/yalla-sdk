package uz.yalla.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiPaginationWrapper<T>(
    val list: List<T>? = null
)
