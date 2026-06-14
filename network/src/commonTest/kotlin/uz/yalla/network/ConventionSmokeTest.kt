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
    fun apiResponse_roundTripsResult() {
        val decoded = json.decodeFromString<ApiResponse<String>>("""{"result":"ok"}""")
        assertEquals("ok", decoded.result)
    }

    @Test
    fun apiResponse_missingResultDecodesToNull() {
        val decoded = json.decodeFromString<ApiResponse<String>>("{}")
        assertNull(decoded.result)
    }

    @Test
    fun apiErrorResponse_decodesMessage() {
        val decoded = json.decodeFromString<ApiErrorResponse>("""{"message":"boom","extra":1}""")
        assertEquals("boom", decoded.message)
    }
}
