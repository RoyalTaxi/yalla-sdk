package uz.yalla.platform.update

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Smoke tests for [AppUpdateState] (part of the [rememberAppUpdateState] expect/actual pair).
 *
 * [AppUpdateState] is a pure Kotlin state holder — no Compose runtime required to test its
 * initial values and contract. Behavioral tests for [rememberAppUpdateState] itself require
 * a Compose UI harness that is not wired in this module; those are deferred to Phase 4.
 */
class AppUpdateStateTest {

    @Test
    fun shouldStartWithNoUpdateAvailable() {
        // Initial state must indicate no update so UIs do not show a banner on first composition.
        val state = AppUpdateState()
        assertFalse(state.isUpdateAvailable, "isUpdateAvailable should default to false")
    }

    @Test
    fun shouldStartWithEmptyStoreUrl() {
        // storeUrl starts empty; populated asynchronously by the platform checker.
        val state = AppUpdateState()
        assertEquals("", state.storeUrl, "storeUrl should default to empty string")
    }

    @Test
    fun shouldStartInCheckingState() {
        // isChecking must be true so UIs can show a loading indicator while the check is in-flight.
        val state = AppUpdateState()
        assertTrue(state.isChecking, "isChecking should default to true")
    }

    @Test
    fun shouldHaveAllThreePropertiesInCorrectDefaultCombination() {
        // The full initial snapshot that a consumer would observe before the first async result.
        val state = AppUpdateState()
        assertFalse(state.isUpdateAvailable)
        assertTrue(state.isChecking)
        assertEquals("", state.storeUrl)
    }
}
