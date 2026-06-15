package uz.yalla.core.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Output-based characterization of [flatMap] and [getOrElse].
 *
 * The load-bearing behavior pinned here is short-circuiting: [flatMap] must NOT invoke its
 * transform on a [Either.Failure], and the original error must propagate untouched (not re-wrapped,
 * not replaced). A regression that ran the transform on failure — or swallowed the original error —
 * would silently corrupt every chained pipeline built on this. [getOrElse] mirrors that contract on
 * the extraction side: the fallback runs only on failure, and receives the actual error value.
 */
class EitherFlatMapTest {
    @Test
    fun flatMapChainsSuccesses() {
        val start: Either<String, Int> = Either.Success(2)
        val result =
            start
                .flatMap { Either.Success(it + 3) }
                .flatMap { Either.Success(it * 10) }

        assertEquals(Either.Success(50), result)
    }

    @Test
    fun flatMapAppliesTransformOnSuccessChangingType() {
        val start: Either<String, Int> = Either.Success(7)
        val result = start.flatMap { Either.Success("value=$it") }

        assertEquals(Either.Success("value=7"), result)
    }

    @Test
    fun flatMapShortCircuitsOnFailureWithoutRunningTransform() {
        var transformRan = false
        val start: Either<String, Int> = Either.Failure("boom")
        val result =
            start.flatMap {
                transformRan = true
                Either.Success(it + 1)
            }

        assertEquals(Either.Failure("boom"), result)
        assertTrue(!transformRan, "transform must not run on Failure")
    }

    @Test
    fun flatMapStopsAtFirstFailureInAChain() {
        var secondTransformRan = false
        val start: Either<String, Int> = Either.Success(1)
        val result: Either<String, Int> =
            start
                .flatMap { _: Int -> Either.Failure<String>("stop at first") }
                .flatMap { value: Int ->
                    secondTransformRan = true
                    Either.Success(value + 100)
                }

        assertEquals(Either.Failure("stop at first"), result)
        assertTrue(!secondTransformRan, "downstream transform must not run after a Failure")
    }

    @Test
    fun flatMapTransformMayItselfProduceFailure() {
        val start: Either<String, Int> = Either.Success(4)
        val result =
            start.flatMap {
                if (it > 3) Either.Failure("too big") else Either.Success(it)
            }

        assertEquals(Either.Failure("too big"), result)
    }

    @Test
    fun getOrElseReturnsDataOnSuccessWithoutRunningFallback() {
        var fallbackRan = false
        val source: Either<String, Int> = Either.Success(42)
        val value =
            source.getOrElse {
                fallbackRan = true
                -1
            }

        assertEquals(42, value)
        assertTrue(!fallbackRan, "fallback must not run on Success")
    }

    @Test
    fun getOrElseRunsFallbackWithErrorOnFailure() {
        val source: Either<String, Int> = Either.Failure("len-source")
        val value = source.getOrElse { it.length }

        assertEquals("len-source".length, value)
    }
}
