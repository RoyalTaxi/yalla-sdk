package uz.yalla.composites.sheet

import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Behavior tests for [DeviceConnectivityState], the moko-connectivity
 * state-holder wrapper. Uses a hand-rolled fake [Connectivity] so the
 * test layer doesn't need a platform connectivity provider.
 *
 * **Dispatcher choice:** these tests use `UnconfinedTestDispatcher` so the
 * collectors launched in `DeviceConnectivityState.init` latch before
 * each `emit` call. With the default `StandardTestDispatcher` the
 * collectors stay suspended until an explicit advance, which makes
 * the assertion-after-emit shape brittle.
 *
 * **Scope choice:** the state-holder collectors are infinite (StateFlow
 * never completes). They run on `backgroundScope` so `runTest` doesn't
 * wait for them at the end of each test.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DeviceConnectivityStateTest {
    private class FakeConnectivity : Connectivity {
        val statusFlow = MutableSharedFlow<Connectivity.Status>(replay = 1)
        val monitoringFlow = MutableStateFlow(false)
        var startCalls = 0
        var stopCalls = 0
        var statusCalls = 0

        override val statusUpdates: SharedFlow<Connectivity.Status>
            get() = statusFlow.asSharedFlow()

        override val monitoring: StateFlow<Boolean>
            get() = monitoringFlow.asStateFlow()

        override suspend fun status(): Connectivity.Status {
            statusCalls++
            return statusFlow.replayCache.firstOrNull()
                ?: Connectivity.Status.Disconnected
        }

        override fun start() {
            startCalls++
            monitoringFlow.value = true
        }

        override fun stop() {
            stopCalls++
            monitoringFlow.value = false
        }
    }

    @Test
    fun initial_state_has_null_status_and_not_monitoring() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            assertNull(state.status)
            assertFalse(state.isConnected)
            assertFalse(state.isDisconnected)
            assertFalse(state.isMonitoring)
        }

    @Test
    fun startMonitoring_calls_provider_start() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            state.startMonitoring()

            assertEquals(1, fake.startCalls)
        }

    @Test
    fun stopMonitoring_calls_provider_stop() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            state.startMonitoring()
            state.stopMonitoring()

            assertEquals(1, fake.startCalls)
            assertEquals(1, fake.stopCalls)
        }

    @Test
    fun forceCheck_invokes_provider_status_in_scope() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            state.forceCheck()

            assertEquals(1, fake.statusCalls)
        }

    @Test
    fun connected_emission_updates_isConnected() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            fake.statusFlow.emit(Connectivity.Status.Connected(metered = false))

            assertTrue(state.isConnected)
            assertFalse(state.isDisconnected)
        }

    @Test
    fun disconnected_emission_updates_isDisconnected() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            fake.statusFlow.emit(Connectivity.Status.Disconnected)

            assertTrue(state.isDisconnected)
            assertFalse(state.isConnected)
        }

    @Test
    fun monitoring_flow_updates_isMonitoring() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            fake.monitoringFlow.value = true
            assertTrue(state.isMonitoring)

            fake.monitoringFlow.value = false
            assertFalse(state.isMonitoring)
        }

    @Test
    fun connected_then_disconnected_transitions_correctly() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val state = DeviceConnectivityState(fake, backgroundScope)

            fake.statusFlow.emit(Connectivity.Status.Connected(metered = false))
            assertTrue(state.isConnected)

            fake.statusFlow.emit(Connectivity.Status.Disconnected)
            assertFalse(state.isConnected)
            assertTrue(state.isDisconnected)
        }

    @Test
    fun multiple_state_holders_observe_same_status() =
        runTest(UnconfinedTestDispatcher()) {
            val fake = FakeConnectivity()
            val a = DeviceConnectivityState(fake, backgroundScope)
            val b = DeviceConnectivityState(fake, backgroundScope)

            fake.statusFlow.emit(Connectivity.Status.Disconnected)

            assertTrue(a.isDisconnected)
            assertTrue(b.isDisconnected)
        }
}
