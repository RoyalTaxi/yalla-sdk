package uz.yalla.core.error

public sealed interface DomainError {
    public data object Connectivity : DomainError

    public data class Remote(
        val message: String? = null,
        val errorCode: String? = null,
        val retryAfter: Int? = null
    ) : DomainError

    public data object Unauthorized : DomainError

    public data object Unknown : DomainError
}
