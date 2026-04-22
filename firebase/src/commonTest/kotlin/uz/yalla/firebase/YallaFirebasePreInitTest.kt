package uz.yalla.firebase

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Verifies that accessing Firebase services before [YallaFirebase.initialize] is called
 * throws [IllegalStateException] with a descriptive message that mentions `initialize()`.
 *
 * These tests rely on the singleton not being initialized in the common test environment
 * (there is no platform Firebase SDK available in commonTest, so [isInitialized] remains false).
 */
class YallaFirebasePreInitTest {

    @Test
    fun analyticsBeforeInitShouldThrowWithClearMessage() {
        // Guard: only run when Firebase is not initialized (common test env has no SDK)
        if (YallaFirebase.isInitialized) return

        val ex = assertFailsWith<IllegalStateException> {
            YallaFirebase.analytics
        }
        assertTrue(
            ex.message?.contains("initialize()") == true,
            "Expected message to mention initialize(), got: ${ex.message}",
        )
    }

    @Test
    fun crashlyticsBeforeInitShouldThrowWithClearMessage() {
        if (YallaFirebase.isInitialized) return

        val ex = assertFailsWith<IllegalStateException> {
            YallaFirebase.crashlytics
        }
        assertTrue(
            ex.message?.contains("initialize()") == true,
            "Expected message to mention initialize(), got: ${ex.message}",
        )
    }

    @Test
    fun messagingBeforeInitShouldThrowWithClearMessage() {
        if (YallaFirebase.isInitialized) return

        val ex = assertFailsWith<IllegalStateException> {
            YallaFirebase.messaging
        }
        assertTrue(
            ex.message?.contains("initialize()") == true,
            "Expected message to mention initialize(), got: ${ex.message}",
        )
    }

    @Test
    fun isInitializedShouldBeFalseBeforeInitialize() {
        // Validate the precondition the other tests depend on
        assertFalse(YallaFirebase.isInitialized)
    }
}
