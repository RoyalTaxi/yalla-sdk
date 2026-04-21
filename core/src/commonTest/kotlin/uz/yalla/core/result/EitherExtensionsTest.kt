package uz.yalla.core.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Tests for the `Either` extensions added in the 0.0.9 generic-order flip:
 * [getOrNull], [getOrThrow], [fold]. Also pins post-flip semantics for
 * [mapSuccess] and [mapFailure] so the generic-parameter order cannot
 * silently regress.
 *
 * Convention reminder: `Either<E, D>` — error first, data second.
 */
class EitherExtensionsTest {
    @Test
    fun shouldReturnDataOnGetOrNullWhenSuccess() {
        val either: Either<String, Int> = Either.Success(42)

        assertEquals(42, either.getOrNull())
    }

    @Test
    fun shouldReturnNullOnGetOrNullWhenFailure() {
        val either: Either<String, Int> = Either.Failure("boom")

        assertNull(either.getOrNull())
    }

    @Test
    fun shouldReturnDataOnGetOrThrowWhenSuccess() {
        val either: Either<String, Int> = Either.Success(42)

        assertEquals(42, either.getOrThrow())
    }

    @Test
    fun shouldThrowOnGetOrThrowWhenFailure() {
        val either: Either<String, Int> = Either.Failure("boom")

        val thrown =
            assertFailsWith<IllegalStateException> {
                either.getOrThrow()
            }
        assertEquals("Either.Failure: boom", thrown.message)
    }

    @Test
    fun shouldCallIfSuccessOnFoldWhenSuccess() {
        val either: Either<String, Int> = Either.Success(7)

        val result =
            either.fold(
                ifFailure = { "err-$it" },
                ifSuccess = { "ok-$it" },
            )

        assertEquals("ok-7", result)
    }

    @Test
    fun shouldCallIfFailureOnFoldWhenFailure() {
        val either: Either<String, Int> = Either.Failure("boom")

        val result =
            either.fold(
                ifFailure = { "err-$it" },
                ifSuccess = { "ok-$it" },
            )

        assertEquals("err-boom", result)
    }

    @Test
    fun shouldPreserveErrorTypeOnMapSuccessAfterFlip() {
        // Post-flip: mapSuccess on Either<E, D> returns Either<E, R>.
        val either: Either<String, Int> = Either.Success(5)

        val result: Either<String, String> = either.mapSuccess { it.toString() }

        assertEquals(Either.Success("5"), result)
    }

    @Test
    fun shouldPreserveDataTypeOnMapFailureAfterFlip() {
        // Post-flip: mapFailure on Either<E, D> returns Either<R, D>.
        val either: Either<String, Int> = Either.Failure("boom")

        val result: Either<Int, Int> = either.mapFailure { it.length }

        assertEquals(Either.Failure(4), result)
    }

    @Test
    fun shouldPreserveSuccessInstanceOnMapFailureWhenSuccess() {
        val either: Either<String, Int> = Either.Success(99)

        val result: Either<Int, Int> = either.mapFailure { it.length }

        assertEquals(Either.Success(99), result)
    }

    @Test
    fun shouldPreserveFailureInstanceOnMapSuccessWhenFailure() {
        val either: Either<String, Int> = Either.Failure("boom")

        val result: Either<String, String> = either.mapSuccess { it.toString() }

        assertEquals(Either.Failure("boom"), result)
    }
}
