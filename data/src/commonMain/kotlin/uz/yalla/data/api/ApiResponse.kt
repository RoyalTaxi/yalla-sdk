package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Generic envelope for single-result API responses.
 *
 * Maps the JSON structure `{ "result": { ... } }` returned by the backend.
 *
 * @param T the type of the wrapped result
 * @property result the response payload, or `null` if absent
 * @since 0.0.1
 */
@Serializable
data class ApiResponse<T>(val result: T? = null)
