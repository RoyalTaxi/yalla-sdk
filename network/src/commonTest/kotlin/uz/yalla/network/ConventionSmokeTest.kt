package uz.yalla.network

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Proves the shared KMP test convention is live for :network — `kotlin("test")` resolves and
 * `commonTest` runs under `testAndroidHostTest`. Characterizes the public envelope contract via a
 * serialization round-trip so the wiring change ships with a real net rather than an empty source set.
 */
class ConventionSmokeTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun apiResponseRoundTripsResult() {
        val decoded = json.decodeFromString<ApiResponse<String>>("""{"result":"ok"}""")
        assertEquals("ok", decoded.result)
    }

    @Test
    fun apiResponseMissingResultDecodesToNull() {
        val decoded = json.decodeFromString<ApiResponse<String>>("{}")
        assertNull(decoded.result)
    }

    @Test
    fun apiErrorResponseDecodesMessage() {
        val decoded = json.decodeFromString<ApiErrorResponse>("""{"message":"boom","extra":1}""")
        assertEquals("boom", decoded.message)
    }

    @Test
    fun apiErrorEnvelopeDecodesTopLevelAndNestedRetryAfter() {
        // Pins the type the wire path actually decodes (parseApiError), including the top-level
        // retry_after used for a flat rate-limit body and the snake_case error_code mapping.
        val decoded =
            json.decodeFromString<ApiErrorEnvelope>(
                """{"code":429,"retry_after":30,"error":{"error_code":"E429"}}"""
            )
        assertEquals(429, decoded.code)
        assertEquals(30, decoded.retryAfter)
        assertEquals("E429", decoded.error?.errorCode)
    }
}
