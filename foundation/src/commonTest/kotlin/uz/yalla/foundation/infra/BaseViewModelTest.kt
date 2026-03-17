package uz.yalla.foundation.infra

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_unexpected
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

private class TestViewModel(
    mapper: DataErrorMapper = DefaultDataErrorMapper()
) : BaseViewModel(mapper)

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldStartWithLoadingFalse() {
        val vm = TestViewModel()
        assertFalse(vm.loading.value)
    }

    @Test
    fun shouldShowLoadingDuringLaunchWithLoading() = runTest {
        val vm = TestViewModel()

        vm.loading.test {
            assertFalse(awaitItem()) // initial

            val job = async {
                with(vm) {
                    vm.safeScope.launchWithLoading(
                        showAfter = 1.milliseconds,
                        minDisplayTime = 1.milliseconds,
                    ) {
                        delay(100.milliseconds)
                    }
                }
            }

            assertTrue(awaitItem()) // loading shown
            assertFalse(awaitItem()) // loading hidden
            job.await()
        }
    }

    @Test
    fun shouldShowErrorDialogOnHandleException() {
        val vm = TestViewModel()

        vm.handleException(RuntimeException("boom"))

        assertTrue(vm.showErrorDialog.value)
        assertEquals(Res.string.error_unexpected, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldShowErrorDialogOnHandleDataError() {
        val vm = TestViewModel()

        vm.handleDataError(DataError.Network.Connection)

        assertTrue(vm.showErrorDialog.value)
        assertEquals(Res.string.error_no_internet, vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldDismissErrorDialog() {
        val vm = TestViewModel()
        vm.handleException(RuntimeException("boom"))

        vm.dismissErrorDialog()

        assertFalse(vm.showErrorDialog.value)
        assertNull(vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldCatchExceptionInSafeScope() = runTest {
        val vm = TestViewModel()

        with(vm) {
            vm.safeScope.launchWithLoading {
                throw RuntimeException("unhandled")
            }
        }

        // Exception caught by handler, not propagated
        assertTrue(vm.showErrorDialog.value)
        assertNotNull(vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldCatchExceptionInLaunchSafe() = runTest {
        val vm = TestViewModel()

        with(vm) {
            vm.safeScope.launchSafe {
                throw RuntimeException("unhandled")
            }
        }

        assertTrue(vm.showErrorDialog.value)
        assertNotNull(vm.currentErrorMessageId.value)
    }

    @Test
    fun shouldMapDataErrorToCorrectMessage() {
        val vm = TestViewModel()

        vm.handleDataError(DataError.Network.Connection)
        assertEquals(Res.string.error_no_internet, vm.currentErrorMessageId.value)

        vm.dismissErrorDialog()

        vm.handleDataError(DataError.Network.Server)
        val serverMsg = vm.currentErrorMessageId.value
        assertNotNull(serverMsg)
        // Server maps to error_server_busy, which is different from Connection's error_no_internet
        assertTrue(serverMsg != Res.string.error_no_internet)
    }
}
