package uz.yalla.firebase.logging

import kotlin.test.Test
import kotlin.test.assertEquals

class YallaFirebaseLoggerTest {

    @Test
    fun shouldNoopLoggerNotThrow() {
        // Should complete without throwing any exception
        YallaFirebaseLogger.Noop.log("tag", "message")
    }

    @Test
    fun shouldCallCustomLoggerWithTagAndMessage() {
        var capturedTag: String? = null
        var capturedMessage: String? = null

        val logger = YallaFirebaseLogger { tag, message ->
            capturedTag = tag
            capturedMessage = message
        }

        logger.log("Analytics", "Failed to log event")

        assertEquals("Analytics", capturedTag)
        assertEquals("Failed to log event", capturedMessage)
    }
}
