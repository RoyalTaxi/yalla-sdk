package uz.yalla.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Characterization of [retryWithBackoff]. The retry policy is safety-critical: only idempotent calls
 * may be retried, only on transient I/O failures, and only up to the attempt limit. Everything else
 * must surface on the first failure so non-idempotent writes are never silently replayed. Virtual
 * time ([runTest]) elides the backoff delays.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RetryWithBackoffTest {
    @Test
    fun returnsImmediatelyWhenFirstAttemptSucceeds() = runTest {
        var calls = 0
        val result = retryWithBackoff(isIdempotent = true) {
            calls++
            "ok"
        }
        assertEquals(1, calls)
        assertEquals("ok", result)
    }

    @Test
    fun retriesIdempotentCallOnTransientIoThenSucceeds() = runTest {
        var calls = 0
        val result = retryWithBackoff(times = 3, isIdempotent = true) {
            calls++
            if (calls < 3) throw IOException("transient") else "ok"
        }
        assertEquals(3, calls)
        assertEquals("ok", result)
    }

    @Test
    fun retriesIdempotentCallUpToLimitThenThrows() = runTest {
        var calls = 0
        assertFailsWith<IOException> {
            retryWithBackoff(times = 3, isIdempotent = true) {
                calls++
                throw IOException("transient")
            }
        }
        assertEquals(3, calls)
    }

    @Test
    fun doesNotRetryNonIdempotentCall() = runTest {
        var calls = 0
        assertFailsWith<IOException> {
            retryWithBackoff(times = 3, isIdempotent = false) {
                calls++
                throw IOException("transient")
            }
        }
        assertEquals(1, calls)
    }

    @Test
    fun doesNotRetryNonTransientException() = runTest {
        var calls = 0
        assertFailsWith<IllegalStateException> {
            retryWithBackoff(times = 3, isIdempotent = true) {
                calls++
                throw IllegalStateException("logic bug")
            }
        }
        assertEquals(1, calls)
    }
}
