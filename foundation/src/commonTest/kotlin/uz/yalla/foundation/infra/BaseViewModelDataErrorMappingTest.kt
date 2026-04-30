package uz.yalla.foundation.infra

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_client_request
import uz.yalla.resources.error_connection_timeout
import uz.yalla.resources.error_data_format
import uz.yalla.resources.error_network_unexpected
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_server_busy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies the [DataError.Network] → string-resource mapping inside
 * [BaseViewModel.handleDataError]. The mapping moved from the deleted
 * `DefaultDataErrorMapper.map` into `BaseViewModel.mapDataErrorToUserMessage`'s
 * `protected open` default body — these tests exercise it through the
 * public `handleDataError` surface.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelDataErrorMappingTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldMapConnectionToNoInternet() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.Connection)
        assertEquals(Res.string.error_no_internet, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapTimeoutToConnectionTimeout() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.Timeout)
        assertEquals(Res.string.error_connection_timeout, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapClientToClientRequest() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.Client)
        assertEquals(Res.string.error_client_request, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapClientWithMessageToClientRequest() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.ClientWithMessage(400, "bad request"))
        assertEquals(Res.string.error_client_request, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapServerToServerBusy() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.Server)
        assertEquals(Res.string.error_server_busy, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapSerializationToDataFormat() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.Serialization)
        assertEquals(Res.string.error_data_format, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapGuestToClientRequest() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.Guest)
        assertEquals(Res.string.error_client_request, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapUnknownToNetworkUnexpected() {
        val vm = MappingTestViewModel()
        vm.handleDataError(DataError.Network.Unknown)
        assertEquals(Res.string.error_network_unexpected, vm.currentErrorMessageId.value)
    }
}

private class MappingTestViewModel : BaseViewModel()
