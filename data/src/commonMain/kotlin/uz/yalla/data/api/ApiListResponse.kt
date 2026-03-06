package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Generic envelope for list API responses.
 *
 * Maps the JSON structure `{ "list": [ ... ] }` returned by the backend.
 *
 * @param T the element type within the list
 * @property list the response items, or `null` if absent
 * @since 0.0.1
 */
@Serializable
data class ApiListResponse<T>(val list: List<T>? = null)
