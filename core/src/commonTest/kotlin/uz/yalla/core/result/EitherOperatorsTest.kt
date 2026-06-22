package uz.yalla.core.result

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EitherOperatorsTest {
    private val success: Either<String, Int> = Either.Success(7)
    private val failure: Either<String, Int> = Either.Failure("boom")

    @Test
    fun onSuccessRunsActionForSuccessAndReturnsSame() {
        var seen: Int? = null
        val returned = success.onSuccess { seen = it }
        assertEquals(7, seen)
        assertEquals(success, returned)
    }

    @Test
    fun onSuccessSkipsActionForFailure() {
        var called = false
        val returned = failure.onSuccess { called = true }
        assertTrue(!called)
        assertEquals(failure, returned)
    }

    @Test
    fun onFailureRunsActionForFailureAndReturnsSame() {
        var seen: String? = null
        val returned = failure.onFailure { seen = it }
        assertEquals("boom", seen)
        assertEquals(failure, returned)
    }

    @Test
    fun onFailureSkipsActionForSuccess() {
        var called = false
        val returned = success.onFailure { called = true }
        assertTrue(!called)
        assertEquals(success, returned)
    }

    @Test
    fun mapSuccessTransformsSuccessAndLeavesFailureUntouched() {
        assertEquals(Either.Success("7!"), success.mapSuccess { "$it!" })
        var called = false
        val mapped =
            failure.mapSuccess {
                called = true
                "$it!"
            }
        assertEquals(Either.Failure("boom"), mapped)
        assertTrue(!called)
    }

    @Test
    fun mapFailureTransformsFailureAndLeavesSuccessUntouched() {
        assertEquals(Either.Failure("boom!"), failure.mapFailure { "$it!" })
        var called = false
        val mapped =
            success.mapFailure {
                called = true
                "$it!"
            }
        assertEquals(Either.Success(7), mapped)
        assertTrue(!called)
    }

    @Test
    fun getOrNullReturnsDataOrNull() {
        assertEquals(7, success.getOrNull())
        assertNull(failure.getOrNull())
    }

    @Test
    fun getOrThrowReturnsDataForSuccess() {
        assertEquals(7, success.getOrThrow())
    }

    @Test
    fun getOrThrowThrowsWithoutLeakingTheErrorPayloadForFailure() {
        val secret: Either<String, Int> = Either.Failure("sk_live_secret_token")
        val thrown = assertFailsWith<IllegalStateException> { secret.getOrThrow() }
        assertTrue(thrown.message?.contains("sk_live_secret_token") != true)
    }

    @Test
    fun foldSelectsBranchByVariant() {
        assertEquals("ok:7", success.fold(ifFailure = { "err:$it" }, ifSuccess = { "ok:$it" }))
        assertEquals("err:boom", failure.fold(ifFailure = { "err:$it" }, ifSuccess = { "ok:$it" }))
    }
}
