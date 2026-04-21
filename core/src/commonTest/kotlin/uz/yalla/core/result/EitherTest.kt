package uz.yalla.core.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class EitherTest {
    @Test
    fun shouldInvokeActionAndReturnSameInstanceWhenSuccess() {
        val either: Either<String, Int> = Either.Success(7)
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
        val either: Either<String, Int> = Either.Failure("error")
        var invocationCount = 0

        either.onSuccess {
            invocationCount++
        }

        assertEquals(0, invocationCount)
    }

    @Test
    fun shouldInvokeActionAndReturnSameInstanceWhenFailure() {
        val either: Either<String, Int> = Either.Failure("error")
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
        val either: Either<String, Int> = Either.Success(42)
        var invocationCount = 0

        either.onFailure {
            invocationCount++
        }

        assertEquals(0, invocationCount)
    }

    @Test
    fun shouldTransformDataOnMapSuccess() {
        val either: Either<String, Int> = Either.Success(5)

        val result = either.mapSuccess { it * 2 }

        assertEquals(Either.Success(10), result)
    }

    @Test
    fun shouldPreserveFailureOnMapSuccess() {
        val either: Either<String, Int> = Either.Failure("error")

        val result = either.mapSuccess { it * 2 }

        assertEquals(Either.Failure("error"), result)
    }

    @Test
    fun shouldTransformErrorOnMapFailure() {
        val either: Either<String, Int> = Either.Failure("error")

        val result = either.mapFailure { it.length }

        assertEquals(Either.Failure(5), result)
    }

    @Test
    fun shouldPreserveSuccessOnMapFailure() {
        val either: Either<String, Int> = Either.Success(42)

        val result = either.mapFailure { it.length }

        assertEquals(Either.Success(42), result)
    }
}
