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

            advanceUntilIdle()
            assertTrue(viewModel.loading.value, "loading must be true while the block runs")

            gate.complete(Unit)
            advanceUntilIdle()
            assertFalse(viewModel.loading.value, "loading must return to false once the block completes")

            viewModel.viewModelScope.cancel()
        }
}
