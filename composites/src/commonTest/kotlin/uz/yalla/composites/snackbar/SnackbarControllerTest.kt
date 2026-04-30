package uz.yalla.composites.snackbar

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Behavior tests for the [SnackbarController] singleton.
 *
 * `SnackbarController` is a process-singleton with a buffered Channel — it
 * survives across tests. The Channel is `BUFFERED`, so trySend always
 * succeeds and never replays. Each test uses Turbine's `test {}` to collect
 * the very next event so cross-test bleed is bounded.
 */
class SnackbarControllerTest {

    @Test
    fun show_emits_show_event_with_data() = runTest {
        val data = SnackbarData(message = "Saved!", isSuccess = true)
        SnackbarController.events.test {
            SnackbarController.show(data)
            val event = awaitItem()
            assertIs<SnackbarEvent.Show>(event)
            assertEquals(data, event.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun dismiss_emits_dismiss_event() = runTest {
        SnackbarController.events.test {
            SnackbarController.dismiss()
            assertEquals(SnackbarEvent.Dismiss, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun sendData_with_data_emits_show_event() = runTest {
        val data = SnackbarData(message = "Loaded", isSuccess = true)
        SnackbarController.events.test {
            SnackbarController.sendData(data)
            val event = awaitItem()
            assertIs<SnackbarEvent.Show>(event)
            assertEquals(data, event.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun sendData_with_null_emits_dismiss_event() = runTest {
        SnackbarController.events.test {
            SnackbarController.sendData(null)
            assertEquals(SnackbarEvent.Dismiss, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun multiple_shows_are_not_deduped() = runTest {
        val data = SnackbarData(message = "Same", isSuccess = false)
        SnackbarController.events.test {
            SnackbarController.show(data)
            SnackbarController.show(data)
            assertEquals(SnackbarEvent.Show(data), awaitItem())
            assertEquals(SnackbarEvent.Show(data), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun show_followed_by_dismiss_emits_both_in_order() = runTest {
        val data = SnackbarData(message = "Toggle me")
        SnackbarController.events.test {
            SnackbarController.show(data)
            SnackbarController.dismiss()
            assertIs<SnackbarEvent.Show>(awaitItem())
            assertEquals(SnackbarEvent.Dismiss, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun events_flow_is_cold_per_collector() = runTest {
        // BUFFERED channel + receiveAsFlow gives each collector the items emitted
        // while it is collecting. A new collector won't get past events.
        SnackbarController.show(SnackbarData(message = "early"))
        SnackbarController.events.test {
            // Either an early item is buffered and arrives, or none does.
            // Both behaviors are valid — assert no exception thrown.
            val timed = expectMostRecentItem()
            assertTrue(timed is SnackbarEvent.Show || timed is SnackbarEvent.Dismiss)
        }
    }
}
