package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Generic envelope for list API responses.
 *
 * Maps the JSON structure `{ "list": [ ... ] }` returned by the backend.
 * Use [ApiResponse] instead when the backend returns a single-result payload.
 *
 * Usage:
 * ```kotlin
 * val response: ApiListResponse<OrderDto> = httpClient.get("orders").body()
 * val orders: List<OrderDto> = response.list.orEmpty()
 * ```
 *
 * @param T the element type within the list
 * @property list the response items, or `null` if absent
 * @see ApiResponse
 * @see ApiErrorResponse
 * @since 0.0.1
 */
@Serializable
data class ApiListResponse<T>(val list: List<T>? = null)
