package uz.yalla.data.network

import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RetryWithBackoffTest {
    @Test
    fun shouldReturnResultOnFirstSuccessfulAttempt() = runTest {
        var callCount = 0
        val result = retryWithBackoff(isIdempotent = true) {
            callCount++
            "ok"
        }

        assertEquals("ok", result)
        assertEquals(1, callCount)
    }

    @Test
    fun shouldRetryOnIOExceptionWhenIdempotent() = runTest {
        var callCount = 0
        val result = retryWithBackoff(
            times = 3,
            initialDelay = 1,
            maxDelay = 10,
            isIdempotent = true,
        ) {
            callCount++
            if (callCount < 3) throw IOException("fail")
            "recovered"
        }

        assertEquals("recovered", result)
        assertEquals(3, callCount)
    }

    @Test
    fun shouldRetryOnSocketTimeoutWhenIdempotent() = runTest {
        var callCount = 0
        val result = retryWithBackoff(
            times = 3,
            initialDelay = 1,
            maxDelay = 10,
            isIdempotent = true,
        ) {
            callCount++
            if (callCount < 2) throw SocketTimeoutException("timeout")
            "recovered"
        }

        assertEquals("recovered", result)
        assertEquals(2, callCount)
    }

    @Test
    fun shouldNotRetryWhenNotIdempotent() = runTest {
        var callCount = 0
        assertFailsWith<IOException> {
            retryWithBackoff(
                times = 3,
                initialDelay = 1,
                isIdempotent = false,
            ) {
                callCount++
                throw IOException("fail")
            }
        }

        assertEquals(1, callCount)
    }

    @Test
    fun shouldNotRetryNonRetryableExceptions() = runTest {
        var callCount = 0
        assertFailsWith<IllegalStateException> {
            retryWithBackoff(
                times = 3,
                initialDelay = 1,
                isIdempotent = true,
            ) {
                callCount++
                throw IllegalStateException("not retryable")
            }
        }

        assertEquals(1, callCount)
    }

    @Test
    fun shouldThrowAfterAllRetriesExhausted() = runTest {
        var callCount = 0
        assertFailsWith<IOException> {
            retryWithBackoff(
                times = 3,
                initialDelay = 1,
                maxDelay = 10,
                isIdempotent = true,
            ) {
                callCount++
                throw IOException("persistent failure")
            }
        }

        assertEquals(3, callCount)
    }
}
