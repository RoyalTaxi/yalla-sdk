package uz.yalla.core.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class EitherTest {
    @Test
    fun shouldInvokeActionAndReturnSameInstanceWhenSuccess() {
        val either: Either<Int, String> = Either.Success(7)
        var capturedValue: Int? = null

        val result =
            either.onSuccess { value ->
                capturedValue = value
            }

        assertSame(either, result)
        assertEquals(7, capturedValue)
    }

    @Test
    fun shouldNotInvokeSuccessActionWhenFailure() {
        val either: Either<Int, String> = Either.Failure("error")
        var invocationCount = 0

        either.onSuccess {
            invocationCount++
        }

        assertEquals(0, invocationCount)
    }

    @Test
    fun shouldInvokeActionAndReturnSameInstanceWhenFailure() {
        val either: Either<Int, String> = Either.Failure("error")
        var capturedError: String? = null

        val result =
            either.onFailure { error ->
                capturedError = error
            }

        assertSame(either, result)
        assertEquals("error", capturedError)
    }

    @Test
    fun shouldNotInvokeFailureActionWhenSuccess() {
        val either: Either<Int, String> = Either.Success(42)
        var invocationCount = 0

        either.onFailure {
            invocationCount++
        }

        assertEquals(0, invocationCount)
    }
}
