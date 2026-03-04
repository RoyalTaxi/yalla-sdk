package uz.yalla.core.error

/**
 * Sealed error hierarchy for all data-layer operations.
 *
 * Used with [uz.yalla.core.result.Either] to provide typed error handling
 * without exceptions in business logic.
 *
 * ## Usage
 * ```kotlin
 * when (error) {
 *     is DataError.Network.Connection -> showNoInternetDialog()
 *     is DataError.Network.ClientWithMessage -> showError(error.message)
 *     is DataError.Network.Guest -> navigateToLogin()
 *     else -> showGenericError()
 * }
 * ```
 *
 * @see uz.yalla.core.result.Either
 * @since 0.0.1
 */
sealed class DataError {
    /**
     * Network-related errors from API calls.
     *
     * Each subtype maps to a specific HTTP or connectivity failure scenario.
     */
    sealed class Network : DataError() {
        /** Device has no internet connection. */
        data object Connection : Network()

        /** Request timed out before server responded. */
        data object Timeout : Network()

        /** Server returned 5xx error. */
        data object Server : Network()

        /** Server returned 4xx error without message body. */
        data object Client : Network()

        /**
         * Server returned 4xx error with a human-readable message.
         *
         * @property code HTTP status code
         * @property message Error message from backend, suitable for UI display
         */
        data class ClientWithMessage(val code: Int, val message: String) : Network()

        /** Response body could not be deserialized. */
        data object Serialization : Network()

        /** User is in guest mode and attempted an authenticated action. */
        data object Guest : Network()

        /** Unclassified network error. */
        data object Unknown : Network()
    }
}
