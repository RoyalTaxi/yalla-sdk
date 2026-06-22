package uz.yalla.network

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun apiErrorEnvelopeDecodesTopLevelAndNestedRetryAfter() {
        val decoded =
            json.decodeFromString<ApiErrorEnvelope>(
                """{"code":429,"retry_after":30,"error":{"error_code":"E429"}}"""
            )
        assertEquals(429, decoded.code)
        assertEquals(30, decoded.retryAfter)
        assertEquals("E429", decoded.error?.errorCode)
    }
}
