package uz.yalla.foundation.infra

import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_client_request
import uz.yalla.resources.error_connection_timeout
import uz.yalla.resources.error_data_format
import uz.yalla.resources.error_network_unexpected
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_server_busy

/**
 * Default implementation mapping [DataError] subtypes to localized string resources.
 *
 * Maps each error category to its corresponding user-facing message.
 *
 * @since 0.0.1
 */
class DefaultDataErrorMapper : DataErrorMapper {
    override fun map(error: DataError): StringResource =
        when (error) {
            DataError.Network.Connection -> Res.string.error_no_internet
            DataError.Network.Timeout -> Res.string.error_connection_timeout
            DataError.Network.Client -> Res.string.error_client_request
            is DataError.Network.ClientWithMessage -> Res.string.error_client_request
            DataError.Network.Server -> Res.string.error_server_busy
            DataError.Network.Serialization -> Res.string.error_data_format
            DataError.Network.Guest -> Res.string.error_client_request
            DataError.Network.Unknown -> Res.string.error_network_unexpected
        }
}
