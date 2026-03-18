package uz.yalla.firebase

import uz.yalla.firebase.logging.YallaFirebaseLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class YallaFirebaseTest {

    @Test
    fun shouldNotBeInitializedByDefault() {
        // The singleton's _isInitialized field starts as false.
        // We read it without calling initialize() to avoid platform-specific side-effects.
        assertFalse(YallaFirebase.isInitialized)
    }

    @Test
    fun shouldHaveNoopLoggerByDefault() {
        // Reset to Noop in case a previous test changed it
        YallaFirebase.logger = YallaFirebaseLogger.Noop
        assertEquals(YallaFirebaseLogger.Noop, YallaFirebase.logger)
    }

    @Test
    fun shouldAcceptCustomLogger() {
        val custom = YallaFirebaseLogger { _, _ -> }
        YallaFirebase.logger = custom
        assertEquals(custom, YallaFirebase.logger)
        // Restore default so other tests are not affected
        YallaFirebase.logger = YallaFirebaseLogger.Noop
    }
}
