package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Error envelope returned by the backend on failed requests.
 *
 * Maps the JSON structure `{ "message": "..." }`. Deserialized
 * by [safeApiCall][uz.yalla.data.network.safeApiCall] to extract
 * error messages from 4xx responses and map them to
 * [DataError.Network.ClientWithMessage][uz.yalla.core.error.DataError.Network.ClientWithMessage].
 *
 * @property message human-readable error description, or `null`
 * @see uz.yalla.data.network.safeApiCall
 * @see ApiResponse
 * @since 0.0.1
 */
@Serializable
data class ApiErrorResponse(val message: String? = null)
