package uz.yalla.core.session

import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals

class UnauthorizedSessionEventsTest {
    @Test
    fun shouldEmitEventWhenPublishIsCalled() =
        runTest {
            drainPendingEventIfExists()

            UnauthorizedSessionEvents.events.test {
                UnauthorizedSessionEvents.publish()

                assertEquals(Unit, awaitItem())
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun shouldKeepOnlySinglePendingEventWhenPublishedRapidlyWithoutCollector() =
        runTest {
            drainPendingEventIfExists()

            repeat(5) {
                UnauthorizedSessionEvents.publish()
            }

            UnauthorizedSessionEvents.events.test {
                assertEquals(Unit, awaitItem())
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }

    private suspend fun drainPendingEventIfExists() {
        withTimeoutOrNull(20) {
            UnauthorizedSessionEvents.events.first()
        }
    }
}
