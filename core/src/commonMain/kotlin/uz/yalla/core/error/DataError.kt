package uz.yalla.core.error

/**
 * Sealed error hierarchy for all data-layer operations.
 *
 * Used with [uz.yalla.core.result.Either] to provide typed error handling
 * without exceptions in business logic. Pattern-match on variants in the
 * feature layer's `intent { }` block and translate to UI-layer side effects;
 * UI code never sees a raw [Throwable].
 *
 * The hierarchy has two broad halves:
 *
 * - **Semantic variants** (top-level) — what happened from the domain's
 *   perspective: [Unauthorized], [Forbidden], [Conflict], [Validation],
 *   [NotFound]. Feature code almost always branches here.
 * - **Transport variants** ([Network]) — what happened at the network layer:
 *   [Network.Connection], [Network.Timeout], [Network.Server],
 *   [Network.Client], [Network.ClientWithMessage], [Network.Serialization],
 *   [Network.Guest], [Network.Unknown]. Used when the failure is infrastructural
 *   rather than semantic (no route, bad body, etc.) or when feature code
 *   wants to show a generic "network problem, retry?" banner.
 *
 * `SafeApiCall` in the data module is the sole producer of these values;
 * it maps HTTP status + response body into the appropriate variant.
 *
 * Naming note: despite the "Data" in the type name, this is the whole
 * domain-layer error contract. The name predates the refactor's
 * `DomainError` proposal in ADR-022; kept as-is to avoid breaking every
 * consumer, with the understanding that `DataError` IS the domain-error
 * type in this codebase. If a future rename becomes worth the churn, it
 * gets its own ADR.
 *
 * ## Usage
 * ```kotlin
 * when (error) {
 *     DataError.Unauthorized       -> navigateToLogin()
 *     is DataError.Forbidden       -> showError("not allowed: ${'$'}{error.reason}")
 *     is DataError.Validation      -> highlightInvalidFields(error.fields)
 *     DataError.NotFound           -> show404Screen()
 *     is DataError.Network         -> showGenericNetworkError(error)
 *     else                         -> showGenericError()
 * }
 * ```
 *
 * @see uz.yalla.core.result.Either
 */
sealed class DataError {
    /**
     * Authentication missing or invalid.
     *
     * Caller-visible meaning: "the session isn't authenticated, surface a
     * login prompt." This is distinct from [Network.Guest] — Guest means
     * the user is intentionally browsing without signing in; Unauthorized
     * means we tried to make an authenticated call and the server rejected
     * us (401 without a retry path).
     *
     * SafeApiCall emits this on 401 when the token is absent, expired, or
     * the server explicitly invalidated it.
     */
    data object Unauthorized : DataError()

    /**
     * Authentication succeeded, but the session lacks permission for the
     * action. Different from [Unauthorized] in that re-login won't help —
     * the server rejects this account.
     *
     * @property reason Optional server-provided reason, suitable for UI display.
     */
    data class Forbidden(
        val reason: String?
    ) : DataError()

    /**
     * Request can't be satisfied in the current server state — the classic
     * 409 case. Usually means "the resource you tried to modify changed
     * under you" (optimistic-locking rejection, duplicate create).
     *
     * @property reason Optional server-provided reason, suitable for UI display.
     */
    data class Conflict(
        val reason: String?
    ) : DataError()

    /**
     * Request shape was valid HTTP but the server rejected the content —
     * 422 or 400 with field-level validation messages.
     *
     * [fields] maps field name → human-readable message, exactly as
     * returned by the server. Feature view models translate keys to
     * locale-specific strings; the SDK does not guess users' languages.
     *
     * @property fields Per-field error messages from the server.
     */
    data class Validation(
        val fields: Map<String, String>
    ) : DataError()

    /**
     * Addressed resource doesn't exist — the semantic 404 case.
     *
     * Note: a literal HTTP 404 without semantic meaning (e.g. endpoint
     * URL typo, not a missing resource) surfaces as [Network.Client]
     * with code=404 instead. SafeApiCall distinguishes by checking the
     * response body: a typed `{ "error": "not_found" }` payload routes
     * here; an empty or server-framework 404 page routes to Network.Client.
     */
    data object NotFound : DataError()

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
