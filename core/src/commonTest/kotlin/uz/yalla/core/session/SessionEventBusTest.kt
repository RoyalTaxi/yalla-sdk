package uz.yalla.core.session

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

            assertEquals(1, first.size)
            assertEquals(1, second.size)
        }

    @Test
    fun doesNotReplayAPastEventToALateSubscriber() =
        runTest {
            val bus = DefaultSessionEventBus()

            bus.publishUnauthorized()

            val received = mutableListOf<Unit>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.unauthorized.toList(received)
                }
            testScheduler.advanceUntilIdle()
            job.cancel()

            assertTrue(received.isEmpty())
        }

    @Test
    fun publishingWithNoCollectorDoesNotSuspendOrThrow() =
        runTest {
            val bus = DefaultSessionEventBus()

            repeat(5) { bus.publishUnauthorized() }

            val received = mutableListOf<Unit>()
            val job =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.unauthorized.toList(received)
                }
            testScheduler.advanceUntilIdle()
            job.cancel()

            assertTrue(received.isEmpty())
        }
}
