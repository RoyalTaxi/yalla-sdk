package uz.yalla.core.error

/**
 * Sealed error hierarchy for all data-layer operations.
 *
 * Used with [uz.yalla.core.result.Either] to provide typed error handling
 * without exceptions in business logic. Pattern-match on variants in the
 * feature layer's `intent { }` block and translate to UI-layer side effects;
 * UI code never sees a raw [Throwable].
 *
 * `SafeApiCall` in the data module is the sole producer of these values;
 * it maps HTTP status + response body into the appropriate variant.
 *
 * ## Usage
 * ```kotlin
 * when (error) {
 *     is DataError.Network.ClientWithMessage -> showError(error.message)
 *     is DataError.Network                   -> showGenericNetworkError(error)
 * }
 * ```
 *
 * @see uz.yalla.core.result.Either
 */
sealed class DataError {
    /**
     * Network-related errors from API calls.
     *
     * Each subtype maps to a specific HTTP or connectivity failure scenario.
     * Used by `safeApiCall` in the data layer to classify HTTP and I/O exceptions.
     */
    sealed class Network : DataError() {
        data object Connection : Network()

        data object Timeout : Network()

        data object Server : Network()

        data object Client : Network()

        /**
         * Server returned 4xx error with a human-readable message.
         *
         * @property code HTTP status code
         * @property message Error message from backend, suitable for UI display
         */
        data class ClientWithMessage(
            val code: Int,
            val message: String
        ) : Network()

        data object Serialization : Network()

        data object Guest : Network()

        data object Unknown : Network()
    }
}
