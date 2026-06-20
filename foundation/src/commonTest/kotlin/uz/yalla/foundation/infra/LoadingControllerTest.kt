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

/**
 * Characterization of [LoadingController]'s anti-flicker spinner machine. Pins the contract a
 * careless edit would silently break: work finishing inside the grace window shows NO spinner; work
 * that outlives grace shows one and holds it for the minimum-visible window; the reference count is
 * released on the normal, exception, and overlapping paths so the spinner can never strand at `true`.
 *
 * Asserts observable behavior through the `loading` [kotlinx.coroutines.flow.StateFlow] only (the
 * counter is private). Virtual time ([runTest]) drives the grace/min-visible delays deterministically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoadingControllerTest {
    private val grace = 200.milliseconds
    private val minVisible = 500.milliseconds

    @Test
    fun fastWorkWithinGracePeriodNeverShowsSpinner() = runTest {
        val controller = LoadingController(this, grace, minVisible)
        runCurrent()

        val gate = CompletableDeferred<Unit>()
        launch { controller.withLoading { gate.await() } }
        runCurrent()

        // Finish before the grace period elapses.
        advanceTimeBy(100.milliseconds)
        gate.complete(Unit)
        advanceTimeBy(1_000.milliseconds)
        runCurrent()

        assertFalse(controller.loading.value, "spinner must not show for sub-grace work")

        coroutineContext.cancelChildren()
    }

    @Test
    fun slowWorkShowsSpinnerAfterGracePeriod() = runTest {
        val controller = LoadingController(this, grace, minVisible)
        runCurrent()

        val gate = CompletableDeferred<Unit>()
        launch { controller.withLoading { gate.await() } }
        runCurrent()

        // Just before grace elapses: still hidden.
        advanceTimeBy(199.milliseconds)
        runCurrent()
        assertFalse(controller.loading.value)

        // After grace elapses: shown.
        advanceTimeBy(2.milliseconds)
        runCurrent()
        assertTrue(controller.loading.value, "spinner must show once work outlives grace")

        gate.complete(Unit)
        coroutineContext.cancelChildren()
    }

    @Test
    fun shownSpinnerStaysVisibleForMinVisibleWindow() = runTest {
        val controller = LoadingController(this, grace, minVisible)
        runCurrent()

        val gate = CompletableDeferred<Unit>()
        launch { controller.withLoading { gate.await() } }
        runCurrent()

        advanceTimeBy(grace + 1.milliseconds)
        runCurrent()
        assertTrue(controller.loading.value)

        // Work completes almost immediately after the spinner showed.
        gate.complete(Unit)
        runCurrent()
        // Still within the min-visible window: must remain shown.
        assertTrue(controller.loading.value, "spinner must hold for the min-visible window")

        advanceTimeBy(minVisible)
        runCurrent()
        assertFalse(controller.loading.value, "spinner hides once min-visible elapses")

        coroutineContext.cancelChildren()
    }

    @Test
    fun throwingBlockStillReleasesTheSpinner() = runTest {
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

        // Body throws; the `finally` decrement must still release the count.
        gate.complete(Unit)
        advanceTimeBy(minVisible + minVisible)
        runCurrent()
        assertTrue(thrown, "the exception must propagate out of withLoading")
        assertFalse(controller.loading.value, "a thrown body must not strand the spinner at true")

        coroutineContext.cancelChildren()
    }

    @Test
    fun overlappingLoadsKeepSpinnerUntilBothFinish() = runTest {
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

        // First finishes; the second is still in flight, so the spinner stays up past min-visible.
        first.complete(Unit)
        advanceTimeBy(minVisible + minVisible)
        runCurrent()
        assertTrue(controller.loading.value, "spinner stays up while any load is in flight")

        // Second finishes; now it can settle.
        second.complete(Unit)
        advanceTimeBy(minVisible + minVisible)
        runCurrent()
        assertFalse(controller.loading.value)

        coroutineContext.cancelChildren()
    }

    @Test
    fun spinnerStartsHiddenWithNoWork() = runTest {
        val controller = LoadingController(this, grace, minVisible)
        runCurrent()
        assertFalse(controller.loading.value)
        coroutineContext.cancelChildren()
    }
}
