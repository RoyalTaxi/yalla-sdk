package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Generic envelope for single-result API responses.
 *
 * Maps the JSON structure `{ "result": { ... } }` returned by the backend.
 * Use [ApiListResponse] instead when the backend returns a list payload.
 *
 * Usage:
 * ```kotlin
 * val response: ApiResponse<UserDto> = httpClient.get("user").body()
 * val user: UserDto? = response.result
 * ```
 *
 * @param T the type of the wrapped result
 * @property result the response payload, or `null` if absent
 * @see ApiListResponse
 * @see ApiErrorResponse
 * @since 0.0.1
 */
@Serializable
data class ApiResponse<T>(val result: T? = null)
