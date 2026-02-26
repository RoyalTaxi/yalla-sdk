package uz.yalla.core.error

sealed class DataError {
    sealed class Network : DataError() {
        data object Connection : Network()
        data object Timeout : Network()
        data object Server : Network()
        data object Client : Network()
        data object Serialization : Network()
        data object Guest : Network()
        data object Unknown : Network()
    }
}
