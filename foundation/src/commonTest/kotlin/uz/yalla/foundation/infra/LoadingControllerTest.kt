package uz.yalla.foundation.infra

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class LoadingControllerTest {
    private val grace = 200.milliseconds
    private val minVisible = 500.milliseconds

    @Test
    fun fastWorkWithinGracePeriodNeverShowsSpinner() =
        runTest {
            val controller = LoadingController(this, grace, minVisible)
            runCurrent()

            val gate = CompletableDeferred<Unit>()
            launch { controller.withLoading { gate.await() } }
            runCurrent()

            advanceTimeBy(100.milliseconds)
            gate.complete(Unit)
            advanceTimeBy(1_000.milliseconds)
            runCurrent()

            assertFalse(controller.loading.value, "spinner must not show for sub-grace work")

            coroutineContext.cancelChildren()
        }

    @Test
    fun slowWorkShowsSpinnerAfterGracePeriod() =
        runTest {
            val controller = LoadingController(this, grace, minVisible)
            runCurrent()

            val gate = CompletableDeferred<Unit>()
            launch { controller.withLoading { gate.await() } }
            runCurrent()

            advanceTimeBy(199.milliseconds)
            runCurrent()
            assertFalse(controller.loading.value)

            advanceTimeBy(2.milliseconds)
            runCurrent()
            assertTrue(controller.loading.value, "spinner must show once work outlives grace")

            gate.complete(Unit)
            coroutineContext.cancelChildren()
        }

    @Test
    fun shownSpinnerStaysVisibleForMinVisibleWindow() =
        runTest {
            val controller = LoadingController(this, grace, minVisible)
            runCurrent()

            val gate = CompletableDeferred<Unit>()
            launch { controller.withLoading { gate.await() } }
            runCurrent()

            advanceTimeBy(grace + 1.milliseconds)
            runCurrent()
            assertTrue(controller.loading.value)

            gate.complete(Unit)
            runCurrent()
            assertTrue(controller.loading.value, "spinner must hold for the min-visible window")

            advanceTimeBy(minVisible)
            runCurrent()
            assertFalse(controller.loading.value, "spinner hides once min-visible elapses")

            coroutineContext.cancelChildren()
        }

    @Test
    fun throwingBlockStillReleasesTheSpinner() =
        runTest {
            val controller = LoadingController(this, grace, minVisible)
            runCurrent()

            val gate = CompletableDeferred<Unit>()
            var thrown = false
            launch {
                try {
                    controller.withLoading {
                        gate.await()
                        error("boom")
                    }
                } catch (_: IllegalStateException) {
                    thrown = true
                }
            }
            runCurrent()

            advanceTimeBy(grace + 1.milliseconds)
            runCurrent()
            assertTrue(controller.loading.value)

            gate.complete(Unit)
            advanceTimeBy(minVisible + minVisible)
            runCurrent()
            assertTrue(thrown, "the exception must propagate out of withLoading")
            assertFalse(controller.loading.value, "a thrown body must not strand the spinner at true")

            coroutineContext.cancelChildren()
        }

    @Test
    fun overlappingLoadsKeepSpinnerUntilBothFinish() =
        runTest {
            val controller = LoadingController(this, grace, minVisible)
            runCurrent()

            val first = CompletableDeferred<Unit>()
            val second = CompletableDeferred<Unit>()
            launch { controller.withLoading { first.await() } }
            launch { controller.withLoading { second.await() } }
            runCurrent()

            advanceTimeBy(grace + 1.milliseconds)
            runCurrent()
            assertTrue(controller.loading.value)

            first.complete(Unit)
            advanceTimeBy(minVisible + minVisible)
            runCurrent()
            assertTrue(controller.loading.value, "spinner stays up while any load is in flight")

            second.complete(Unit)
            advanceTimeBy(minVisible + minVisible)
            runCurrent()
            assertFalse(controller.loading.value)

            coroutineContext.cancelChildren()
        }

    @Test
    fun spinnerStartsHiddenWithNoWork() =
        runTest {
            val controller = LoadingController(this, grace, minVisible)
            runCurrent()
            assertFalse(controller.loading.value)
            coroutineContext.cancelChildren()
        }
}
