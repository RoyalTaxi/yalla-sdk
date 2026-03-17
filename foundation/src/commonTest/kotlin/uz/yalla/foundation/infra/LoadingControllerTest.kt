package uz.yalla.foundation.infra

import app.cash.turbine.test
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class LoadingControllerTest {

    private fun controller() = LoadingController(
        showAfter = 50.milliseconds,
        minDisplayTime = 50.milliseconds,
    )

    @Test
    fun shouldNotShowLoadingForFastOperation() = runTest {
        val ctrl = controller()

        ctrl.loading.test {
            assertEquals(false, awaitItem())

            ctrl.withLoading(showAfter = 50.milliseconds) {
                delay(10.milliseconds)
            }

            expectNoEvents()
        }
    }

    @Test
    fun shouldShowLoadingForSlowOperation() = runTest {
        val ctrl = controller()

        ctrl.loading.test {
            assertFalse(awaitItem())

            val job = async {
                ctrl.withLoading(showAfter = 10.milliseconds) {
                    delay(200.milliseconds)
                }
            }

            assertTrue(awaitItem())
            assertFalse(awaitItem())
            job.await()
        }
    }

    @Test
    fun shouldRespectMinDisplayTime() = runTest {
        val ctrl = LoadingController(
            showAfter = 1.milliseconds,
            minDisplayTime = 100.milliseconds,
        )

        ctrl.loading.test {
            assertFalse(awaitItem())

            val job = async {
                ctrl.withLoading(
                    showAfter = 1.milliseconds,
                    minDisplayTime = 100.milliseconds,
                ) {
                    delay(20.milliseconds)
                }
            }

            assertTrue(awaitItem())
            // Should stay true for minDisplayTime even though block finished
            assertFalse(awaitItem())
            job.await()
        }
    }

    @Test
    fun shouldReturnBlockResult() = runTest {
        val ctrl = controller()
        val result = ctrl.withLoading { "hello" }
        assertEquals("hello", result)
    }

    @Test
    fun shouldHandleConcurrentOperations() = runTest {
        val ctrl = LoadingController(
            showAfter = 1.milliseconds,
            minDisplayTime = 1.milliseconds,
        )

        ctrl.loading.test {
            assertFalse(awaitItem())

            val job1 = async {
                ctrl.withLoading(showAfter = 1.milliseconds, minDisplayTime = 1.milliseconds) {
                    delay(100.milliseconds)
                }
            }
            val job2 = async {
                ctrl.withLoading(showAfter = 1.milliseconds, minDisplayTime = 1.milliseconds) {
                    delay(150.milliseconds)
                }
            }

            // Loading becomes true
            assertTrue(awaitItem())

            job1.await()
            job2.await()

            // Loading becomes false only after both complete
            assertFalse(awaitItem())
        }
    }

    @Test
    fun shouldNotDeadlockWithOverlappingOperations() = runTest {
        val ctrl = LoadingController(
            showAfter = 1.milliseconds,
            minDisplayTime = 50.milliseconds,
        )

        // First operation triggers minDisplayTime delay
        val job1 = async {
            ctrl.withLoading(showAfter = 1.milliseconds, minDisplayTime = 50.milliseconds) {
                delay(20.milliseconds)
            }
        }

        delay(30.milliseconds) // Let first op finish, enter minDisplayTime delay

        // Second operation starts while first is in minDisplayTime delay
        // This would deadlock if delay was inside mutex
        val job2 = async {
            ctrl.withLoading(showAfter = 1.milliseconds, minDisplayTime = 1.milliseconds) {
                delay(10.milliseconds)
            }
        }

        job1.await()
        job2.await()

        // If we got here, no deadlock
        assertTrue(true)
    }

    @Test
    fun shouldStartWithLoadingFalse() = runTest {
        val ctrl = controller()
        assertFalse(ctrl.loading.value)
    }
}
