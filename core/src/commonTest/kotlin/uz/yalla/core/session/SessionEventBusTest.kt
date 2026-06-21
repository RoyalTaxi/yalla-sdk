package uz.yalla.core.session

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Characterization of [DefaultSessionEventBus], the SDK-wide 401/unauthorized broadcast that
 * drives forced logout.
 *
 * Pins the three load-bearing semantics the migration off a `Channel(CONFLATED)` had to preserve
 * or fix (review M12/M13):
 *  - broadcast: every active collector receives each publish (a `Channel`'s fan-out delivered to
 *    exactly one collector, letting a second observer silently miss logout);
 *  - no cross-session replay: a subscriber that joins after a publish does NOT see the past event,
 *    so a stale `unauthorized` cannot force-logout a brand-new session;
 *  - non-suspending publish that coalesces a burst with no collector into at most one pending
 *    event (the conflation behavior callers relied on).
 */
class SessionEventBusTest {
    @Test
    fun deliversToTheActiveCollector() =
        runTest {
            val bus = DefaultSessionEventBus()
            val received = mutableListOf<Unit>()

            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.unauthorized.take(1).toList(received)
                }

            bus.publishUnauthorized()
            job.join()

            assertEquals(1, received.size)
        }

    @Test
    fun broadcastsEachEventToEveryActiveCollector() =
        runTest {
            val bus = DefaultSessionEventBus()
            val first = mutableListOf<Unit>()
            val second = mutableListOf<Unit>()

            val firstJob =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.unauthorized.take(1).toList(first)
                }
            val secondJob =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.unauthorized.take(1).toList(second)
                }

            bus.publishUnauthorized()
            firstJob.join()
            secondJob.join()

            // Broadcast: BOTH collectors see the event. A fan-out Channel would deliver to only one.
            assertEquals(1, first.size)
            assertEquals(1, second.size)
        }

    @Test
    fun doesNotReplayAPastEventToALateSubscriber() =
        runTest {
            val bus = DefaultSessionEventBus()

            // Published while nobody is collecting — must NOT be retained for the next subscriber.
            bus.publishUnauthorized()

            val received = mutableListOf<Unit>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.unauthorized.toList(received)
                }
            testScheduler.advanceUntilIdle()
            job.cancel()

            // replay = 0: a fresh subscriber never sees the stale event from a past session.
            assertTrue(received.isEmpty())
        }

    @Test
    fun publishingWithNoCollectorDoesNotSuspendOrThrow() =
        runTest {
            val bus = DefaultSessionEventBus()

            // tryEmit must never suspend or fail, even for a burst with no active collector.
            repeat(5) { bus.publishUnauthorized() }

            val received = mutableListOf<Unit>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.unauthorized.toList(received)
                }
            testScheduler.advanceUntilIdle()
            job.cancel()

            // No replay, so the buffered burst is not redelivered to a later subscriber either.
            assertTrue(received.isEmpty())
        }
}
