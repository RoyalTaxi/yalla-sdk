package uz.yalla.capabilities.connectivity

import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Behavioral characterization of [ConnectivityState].
 *
 * Pins that `isOnline` tracks the collaborator's status emissions, that [refresh]
 * reflects a one-shot read, and — guarding the M16 fix — that a refresh is
 * single-flight (only the latest write survives concurrent refreshes).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ConnectivityStateTest {
    private class FakeConnectivity(
        private val oneShotStatus: Connectivity.Status
    ) : Connectivity {
        val emissions = MutableSharedFlow<Connectivity.Status>(replay = 0, extraBufferCapacity = 8)
        override val statusUpdates: SharedFlow<Connectivity.Status> = emissions.asSharedFlow()
        override val monitoring: StateFlow<Boolean> = MutableStateFlow(true)

        override suspend fun status(): Connectivity.Status = oneShotStatus

        override fun start() = Unit

        override fun stop() = Unit
    }

    @Test
    fun isOnlineTracksStatusEmissions() = runTest {
        val fake = FakeConnectivity(Connectivity.Status.Disconnected)
        val state = ConnectivityState(fake, this)

        runCurrent()
        fake.emissions.emit(Connectivity.Status.Disconnected)
        runCurrent()
        assertFalse(state.isOnline)

        fake.emissions.emit(Connectivity.Status.Connected(metered = false))
        runCurrent()
        assertTrue(state.isOnline)

        coroutineContext.cancelChildren()
    }

    @Test
    fun refreshReflectsOneShotStatusRead() = runTest {
        val fake = FakeConnectivity(Connectivity.Status.Disconnected)
        val state = ConnectivityState(fake, this)

        state.refresh().join()
        assertFalse(state.isOnline)

        coroutineContext.cancelChildren()
    }

    @Test
    fun refreshIsSingleFlightAndDoesNotLeakJobs() = runTest {
        val fake = FakeConnectivity(Connectivity.Status.Connected(metered = false))
        val state = ConnectivityState(fake, this)

        val first = state.refresh()
        val second = state.refresh()
        // Launching a second refresh must cancel the in-flight first one.
        assertTrue(first.isCancelled)

        second.join()
        assertTrue(state.isOnline)

        coroutineContext.cancelChildren()
    }
}
