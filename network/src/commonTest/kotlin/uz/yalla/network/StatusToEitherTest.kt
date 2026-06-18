package uz.yalla.network

import uz.yalla.network.error.DataError
import uz.yalla.core.result.Either
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Output-based tests over the Humble Object's pure decision: `statusToEither` carries the whole
 * HTTP-status → result mapping that `safeApiCall` used to inline, so it can be exercised by value
 * alone — no Ktor client, no suspension, no I/O. These cases pin the contract the suspend
 * orchestration now leans on: which status buckets succeed, which fail, and that a parsed envelope
 * overrides the bucket default while a missing body falls back to it.
 */
class StatusToEitherTest {
    @Test
    fun success2xx_signalsDeserializeBody() {
        assertEquals(Either.Success(Unit), statusToEither(200, parsedError = null))
        assertEquals(Either.Success(Unit), statusToEither(201, parsedError = null))
        assertEquals(Either.Success(Unit), statusToEither(299, parsedError = null))
    }

    @Test
    fun redirect3xx_isClientFailure() {
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(301, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(399, parsedError = null))
    }

    @Test
    fun clientError4xx_noBody_isClientFailure() {
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(400, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(404, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(499, parsedError = null))
    }

    @Test
    fun clientError4xx_withParsedBody_keepsServerDetail() {
        val parsed = DataError.Network.Api(code = 404, message = "not found", errorCode = "E404", retryAfter = null)
        assertEquals(Either.Failure(parsed), statusToEither(404, parsed))
    }

    @Test
    fun serverError5xx_noBody_isServerFailure() {
        assertEquals(Either.Failure(DataError.Network.Server), statusToEither(500, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Server), statusToEither(599, parsedError = null))
    }

    @Test
    fun serverError5xx_withParsedBody_keepsServerDetail() {
        val parsed = DataError.Network.Api(code = 503, message = "unavailable", errorCode = "E503", retryAfter = 30)
        assertEquals(Either.Failure(parsed), statusToEither(503, parsed))
    }

    @Test
    fun outOfRangeStatus_noBody_isUnknownFailure() {
        assertEquals(Either.Failure(DataError.Network.Unknown), statusToEither(199, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Unknown), statusToEither(600, parsedError = null))
    }

    @Test
    fun isSuccessStatus_marksOnly2xx() {
        assertEquals(true, isSuccessStatus(200))
        assertEquals(true, isSuccessStatus(299))
        assertEquals(false, isSuccessStatus(199))
        assertEquals(false, isSuccessStatus(300))
    }
}
