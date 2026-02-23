package uz.yalla.core.error

sealed class DataError {
    sealed class Network : DataError() {
        data object Unauthorized : Network()

        data object NoInternet : Network()

        data object Timeout : Network()

        data object ServerError : Network()

        data object ClientError : Network()

        data object SerializationError : Network()

        data object InsufficientBalance : Network()

        data class Unknown(
            val message: String? = null
        ) : Network()
    }

    sealed class Local : DataError() {
        data object NotFound : Local()

        data object DatabaseError : Local()
    }
}
