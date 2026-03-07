package uz.yalla.data.api

import kotlinx.serialization.Serializable

/**
 * Error envelope returned by the backend on failed requests.
 *
 * Maps the JSON structure `{ "message": "..." }`.
 *
 * @property message human-readable error description, or `null`
 * @since 0.0.1
 */
@Serializable
data class ApiErrorResponse(val message: String? = null)
