package uz.yalla.core.error

/** The SDK's domain failure taxonomy — the error side of every `Either<DomainError, T>`. */
public sealed interface DomainError {
    /** No usable network connection (offline, timeout, DNS). */
    public data object Connectivity : DomainError

    /** The backend returned an error, optionally with a [message], [errorCode], and [retryAfter] hint (seconds). */
    public data class Remote(
        val message: String? = null,
        val errorCode: String? = null,
        val retryAfter: Int? = null
    ) : DomainError

    /** The session is invalid/expired (HTTP 401) — drives forced logout. */
    public data object Unauthorized : DomainError

    /** An unclassified failure. */
    public data object Unknown : DomainError
}
