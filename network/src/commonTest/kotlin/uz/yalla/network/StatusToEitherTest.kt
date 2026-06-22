package uz.yalla.network

import uz.yalla.core.result.Either
import uz.yalla.network.error.DataError
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusToEitherTest {
    @Test
    fun success2xxSignalsDeserializeBody() {
        assertEquals(Either.Success(Unit), statusToEither(200, parsedError = null))
        assertEquals(Either.Success(Unit), statusToEither(201, parsedError = null))
        assertEquals(Either.Success(Unit), statusToEither(299, parsedError = null))
    }

    @Test
    fun redirect3xxIsClientFailure() {
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(301, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(399, parsedError = null))
    }

    @Test
    fun clientError4xxWithoutBodyIsClientFailure() {
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(400, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(404, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Client), statusToEither(499, parsedError = null))
    }

    @Test
    fun clientError4xxWithParsedBodyKeepsServerDetail() {
        val parsed = DataError.Network.Api(code = 404, message = "not found", errorCode = "E404", retryAfter = null)
        assertEquals(Either.Failure(parsed), statusToEither(404, parsed))
    }

    @Test
    fun serverError5xxWithoutBodyIsServerFailure() {
        assertEquals(Either.Failure(DataError.Network.Server), statusToEither(500, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Server), statusToEither(599, parsedError = null))
    }

    @Test
    fun serverError5xxWithParsedBodyKeepsServerDetail() {
        val parsed = DataError.Network.Api(code = 503, message = "unavailable", errorCode = "E503", retryAfter = 30)
        assertEquals(Either.Failure(parsed), statusToEither(503, parsed))
    }

    @Test
    fun outOfRangeStatusWithoutBodyIsUnknownFailure() {
        assertEquals(Either.Failure(DataError.Network.Unknown), statusToEither(199, parsedError = null))
        assertEquals(Either.Failure(DataError.Network.Unknown), statusToEither(600, parsedError = null))
    }

    @Test
    fun isSuccessStatusMarksOnly2xx() {
        assertEquals(true, isSuccessStatus(200))
        assertEquals(true, isSuccessStatus(299))
        assertEquals(false, isSuccessStatus(199))
        assertEquals(false, isSuccessStatus(300))
    }
}
