package uz.yalla.network.error

public sealed class DataError {
    public sealed class Network : DataError() {
        public data object Connection : Network()

        public data object Timeout : Network()

        public data object Server : Network()

        public data object Client : Network()

        public data object Serialization : Network()

        public data object Guest : Network()

        public data object Unauthorized : Network()

        public data object Unknown : Network()

        public data class Api(
            val code: Int? = null,
            val message: String? = null,
            val errorCode: String? = null,
            val retryAfter: Int? = null
        ) : Network()
    }
}
