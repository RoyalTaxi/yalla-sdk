package uz.yalla.foundation.infra

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Characterization of [BaseViewModel]'s two crash/loading contracts — the substrate every product
 * ViewModel inherits. Pins that [BaseViewModel.safeScope] is the log-and-swallow hatch (a throwing
 * coroutine on it does not crash the scope or cancel sibling work), and that [launchWithLoading]
 * drives [BaseViewModel.loading] true while in flight and false once it completes. A regression that
 * drops the handler or the loading wiring would otherwise ship with a green build.
 *
 * `Main` is backed by [runTest]'s own scheduler so the single virtual clock drives both
 * `viewModelScope` (which inherits `Main`) and the test body — the loading delays advance with
 * [advanceUntilIdle].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {
    private class TestViewModel : BaseViewModel()

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun safeScopeSwallowsThrowingWorkAndKeepsSiblingsRunning() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val viewModel = TestViewModel()
            var siblingRan = false

            viewModel.safeScope.launch { error("boom in fire-and-forget work") }
            viewModel.safeScope.launch { siblingRan = true }
            runCurrent()

            // The throwing coroutine was caught by the handler; the sibling still ran, the scope is alive.
            assertTrue(siblingRan, "safeScope must swallow the failure and keep sibling work running")
            assertTrue(viewModel.safeScope.coroutineContext[Job]?.isActive ?: false)

            viewModel.viewModelScope.cancel()
        }

    @Test
    fun launchWithLoadingTogglesLoadingAroundTheBlock() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            val viewModel = TestViewModel()
            assertFalse(viewModel.loading.value)

            val gate = CompletableDeferred<Unit>()
            with(viewModel) { viewModel.launchWithLoading { gate.await() } }

            // Let the grace period elapse: loading turns on while the block is in flight.
            advanceUntilIdle()
            assertTrue(viewModel.loading.value, "loading must be true while the block runs")

            gate.complete(Unit)
            advanceUntilIdle()
            assertFalse(viewModel.loading.value, "loading must return to false once the block completes")

            viewModel.viewModelScope.cancel()
        }
}
