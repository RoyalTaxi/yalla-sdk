package uz.yalla.foundation.viewmodel

import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_application
import uz.yalla.resources.error_client_request
import uz.yalla.resources.error_connection_timeout
import uz.yalla.resources.error_data_format
import uz.yalla.resources.error_network_unexpected
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_server_busy
import uz.yalla.resources.error_session_expired
import uz.yalla.resources.error_unknown

/**
 * Default implementation of [DataErrorMapper].
 *
 * Maps each [DataError] variant to the corresponding SDK string resource.
 */
class DefaultDataErrorMapper : DataErrorMapper {
    override fun map(error: DataError): StringResource =
        when (error) {
            DataError.Network.NoInternet -> Res.string.error_no_internet
            DataError.Network.Timeout -> Res.string.error_connection_timeout
            DataError.Network.Unauthorized -> Res.string.error_session_expired
            DataError.Network.ClientError -> Res.string.error_client_request
            DataError.Network.ServerError -> Res.string.error_server_busy
            DataError.Network.SerializationError -> Res.string.error_data_format
            is DataError.Network.Unknown -> Res.string.error_network_unexpected
            DataError.Network.InsufficientBalance -> Res.string.error_client_request
            DataError.Local.NotFound -> Res.string.error_unknown
            DataError.Local.DatabaseError -> Res.string.error_application
        }
}
