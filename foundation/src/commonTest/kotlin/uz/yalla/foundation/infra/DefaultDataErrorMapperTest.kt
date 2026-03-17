package uz.yalla.foundation.infra

import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_client_request
import uz.yalla.resources.error_connection_timeout
import uz.yalla.resources.error_data_format
import uz.yalla.resources.error_network_unexpected
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_server_busy
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultDataErrorMapperTest {
    private val mapper = DefaultDataErrorMapper()

    @Test
    fun shouldMapConnectionToNoInternet() {
        assertEquals(Res.string.error_no_internet, mapper.map(DataError.Network.Connection))
    }

    @Test
    fun shouldMapTimeoutToConnectionTimeout() {
        assertEquals(Res.string.error_connection_timeout, mapper.map(DataError.Network.Timeout))
    }

    @Test
    fun shouldMapClientToClientRequest() {
        assertEquals(Res.string.error_client_request, mapper.map(DataError.Network.Client))
    }

    @Test
    fun shouldMapClientWithMessageToClientRequest() {
        assertEquals(
            Res.string.error_client_request,
            mapper.map(DataError.Network.ClientWithMessage(400, "bad request"))
        )
    }

    @Test
    fun shouldMapServerToServerBusy() {
        assertEquals(Res.string.error_server_busy, mapper.map(DataError.Network.Server))
    }

    @Test
    fun shouldMapSerializationToDataFormat() {
        assertEquals(Res.string.error_data_format, mapper.map(DataError.Network.Serialization))
    }

    @Test
    fun shouldMapGuestToClientRequest() {
        assertEquals(Res.string.error_client_request, mapper.map(DataError.Network.Guest))
    }

    @Test
    fun shouldMapUnknownToNetworkUnexpected() {
        assertEquals(Res.string.error_network_unexpected, mapper.map(DataError.Network.Unknown))
    }
}
