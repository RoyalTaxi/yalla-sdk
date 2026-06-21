package uz.yalla.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Characterization of [retryWithBackoff]. The retry policy is safety-critical: only idempotent calls
 * may be retried, only on transient I/O failures, and only up to the attempt limit. Everything else
 * must surface on the first failure so non-idempotent writes are never silently replayed. Virtual
 * time ([runTest]) elides the backoff delays.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RetryWithBackoffTest {
    @Test
    fun returnsImmediatelyWhenFirstAttemptSucceeds() =
        runTest {
            var calls = 0
            val result =
                retryWithBackoff(isIdempotent = true) {
                    calls++
                    "ok"
                }
            assertEquals(1, calls)
            assertEquals("ok", result)
        }

    @Test
    fun retriesIdempotentCallOnTransientIoThenSucceeds() =
        runTest {
            var calls = 0
            val result =
                retryWithBackoff(times = 3, isIdempotent = true) {
                    calls++
                    if (calls < 3) throw IOException("transient") else "ok"
                }
            assertEquals(3, calls)
            assertEquals("ok", result)
        }

    @Test
    fun retriesIdempotentCallUpToLimitThenThrows() =
        runTest {
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
    fun doesNotRetryNonIdempotentCall() =
        runTest {
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
    fun doesNotRetryNonTransientException() =
        runTest {
            var calls = 0
            assertFailsWith<IllegalStateException> {
                retryWithBackoff(times = 3, isIdempotent = true) {
                    calls++
                    error("logic bug")
                }
            }
            assertEquals(1, calls)
        }

    @Test
    fun timesOneRunsExactlyOnceWithNoRetryEvenWhenIdempotent() =
        runTest {
            var calls = 0
            assertFailsWith<IOException> {
                retryWithBackoff(times = 1, isIdempotent = true) {
                    calls++
                    throw IOException("transient")
                }
            }
            // repeat(times - 1) = repeat(0): only the final out-of-loop attempt runs.
            assertEquals(1, calls)
        }

    @Test
    fun noSingleBackoffDelayExceedsMaxDelay() =
        runTest {
            val maxDelay = 2_000L
            val attemptTimes = mutableListOf<Long>()
            assertFailsWith<IOException> {
                retryWithBackoff(times = 8, maxDelay = maxDelay, isIdempotent = true) {
                    attemptTimes += testScheduler.currentTime
                    throw IOException("transient")
                }
            }
            // The realized sleep (currentDelay + jitter, capped) between consecutive attempts must
            // never exceed the documented cap — jitter on top of a capped base previously hit ~1.5x.
            attemptTimes.zipWithNext { prev, next ->
                assertTrue(next - prev <= maxDelay, "delay ${next - prev} exceeded cap $maxDelay")
            }
        }
}
