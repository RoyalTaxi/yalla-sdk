package uz.yalla.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiErrorEnvelope(
    val code: Int? = null,
    val message: String? = null,
    @SerialName("retry_after") val retryAfter: Int? = null,
    val error: ApiErrorBody? = null
)

@Serializable
internal data class ApiErrorBody(
    @SerialName("error_code") val errorCode: String? = null,
    @SerialName("retry_after") val retryAfter: Int? = null
)
