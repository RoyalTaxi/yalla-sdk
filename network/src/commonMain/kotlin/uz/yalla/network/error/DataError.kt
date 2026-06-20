package uz.yalla.network.error

/** The error taxonomy every SDK call returns on the failure side of an `Either`. */
public sealed class DataError {
    /** A failure originating from the network layer (transport, HTTP status, decode, or policy). */
    public sealed class Network : DataError() {
        /** The request could not reach the server (no connectivity, DNS, refused connection). */
        public data object Connection : Network()

        /** The request or socket timed out before a response arrived. */
        public data object Timeout : Network()

        /** The server returned a 5xx with no usable error envelope. */
        public data object Server : Network()

        /** The request was rejected with a 3xx/4xx (other than 401) and no usable error envelope. */
        public data object Client : Network()

        /** The response body could not be decoded as the expected type (malformed or empty). */
        public data object Serialization : Network()

        /** A guest-mode request targeted an endpoint outside the guest allowlist (blocked before sending). */
        public data object Guest : Network()

        /**
         * The session is unauthenticated/expired (HTTP 401). Callers should branch to logout/re-auth
         * rather than treating it like an ordinary [Client] error.
         */
        public data object Unauthorized : Network()

        /** A failure that does not fit any other bucket. */
        public data object Unknown : Network()

        /** A structured server error envelope; carries server-supplied code/message/retry detail. */
        public data class Api(
            val code: Int? = null,
            val message: String? = null,
            val errorCode: String? = null,
            val retryAfter: Int? = null
        ) : Network()
    }
}
