package uz.yalla.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import uz.yalla.core.result.Either
import uz.yalla.network.error.DataError
import kotlin.test.Test
import kotlin.test.assertEquals

class SafeApiCallWireTest {
    @Serializable
    private data class Dto(
        val name: String
    )

    private fun jsonClient(
        status: HttpStatusCode,
        body: String
    ): HttpClient =
        HttpClient(MockEngine { respond(body, status, JSON_HEADERS) }) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }

    @Test
    fun success2xxWithJsonBodyDeserializesToT() =
        runTest {
            val result: Either<DataError.Network, Dto> =
                safeApiCall { jsonClient(HttpStatusCode.OK, """{"name":"yalla"}""").get("x") }
            assertEquals(Either.Success(Dto("yalla")), result)
        }

    @Test
    fun success2xxWithUnitTDoesNotTouchTheBody() =
        runTest {
            val client = HttpClient(MockEngine { respond("not json", HttpStatusCode.OK) })
            val result: Either<DataError.Network, Unit> = safeApiCall { client.get("x") }
            assertEquals(Either.Success(Unit), result)
        }

    @Test
    fun emptyBodyWithNonUnitTIsSerializationFailureNotCrash() =
        runTest {
            val client = HttpClient(MockEngine { respond("", HttpStatusCode.NoContent) })
            val result: Either<DataError.Network, Dto> = safeApiCall { client.get("x") }
            assertEquals(Either.Failure(DataError.Network.Serialization), result)
        }

    @Test
    fun malformedJsonBodyIsSerializationFailure() =
        runTest {
            val result: Either<DataError.Network, Dto> =
                safeApiCall { jsonClient(HttpStatusCode.OK, """{"name":}""").get("x") }
            assertEquals(Either.Failure(DataError.Network.Serialization), result)
        }

    @Test
    fun connectionDropIsConnectionFailure() =
        runTest {
            val client = HttpClient(MockEngine { throw IOException("connection reset") })
            val result: Either<DataError.Network, Dto> = safeApiCall { client.get("x") }
            assertEquals(Either.Failure(DataError.Network.Connection), result)
        }

    @Test
    fun socketTimeoutIsTimeoutFailure() =
        runTest {
            val client = HttpClient(MockEngine { throw SocketTimeoutException("timed out") })
            val result: Either<DataError.Network, Dto> = safeApiCall { client.get("x") }
            assertEquals(Either.Failure(DataError.Network.Timeout), result)
        }

    @Test
    fun unauthorized401MapsToUnauthorizedNotClient() =
        runTest {
            val client = HttpClient(MockEngine { respond("", HttpStatusCode.Unauthorized) })
            val result: Either<DataError.Network, Unit> = safeApiCall { client.get("x") }
            assertEquals(Either.Failure(DataError.Network.Unauthorized), result)
        }

    @Test
    fun clientError404KeepsServerEnvelopeDetail() =
        runTest {
            val body = """{"code":404,"message":"not found","error":{"error_code":"E404"}}"""
            val result: Either<DataError.Network, Unit> =
                safeApiCall { jsonClient(HttpStatusCode.NotFound, body).get("x") }
            assertEquals(
                Either.Failure(DataError.Network.Api(code = 404, message = "not found", errorCode = "E404")),
                result
            )
        }

    @Test
    fun flatRateLimitEnvelopeWithOnlyCodeAndRetryAfterSurvives() =
        runTest {
            val body = """{"code":429,"retry_after":30}"""
            val result: Either<DataError.Network, Unit> =
                safeApiCall { jsonClient(HttpStatusCode.TooManyRequests, body).get("x") }
            assertEquals(
                Either.Failure(DataError.Network.Api(code = 429, retryAfter = 30)),
                result
            )
        }

    @Test
    fun serverError500WithoutBodyIsServerFailure() =
        runTest {
            val client = HttpClient(MockEngine { respond("", HttpStatusCode.InternalServerError) })
            val result: Either<DataError.Network, Unit> = safeApiCall { client.get("x") }
            assertEquals(Either.Failure(DataError.Network.Server), result)
        }

    private companion object {
        val JSON_HEADERS = headersOf(HttpHeaders.ContentType, "application/json")
    }
}
