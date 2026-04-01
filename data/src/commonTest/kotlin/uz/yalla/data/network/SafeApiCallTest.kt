package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import uz.yalla.core.error.DataError
import uz.yalla.core.result.Either
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@Serializable
internal data class SafeApiCallTestResponse(val id: Int, val name: String)

class SafeApiCallTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    // --- Status code path (expectSuccess = false) ---

    @Test
    fun shouldReturnSuccessOnHttp200() = runTest {
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("""{"id":1,"name":"test"}""", HttpStatusCode.OK, jsonHeaders) }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Success<SafeApiCallTestResponse>>(result)
        assertEquals(1, result.data.id)
        assertEquals("test", result.data.name)
    }

    @Test
    fun shouldReturnSuccessForUnitResponse() = runTest {
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("", HttpStatusCode.OK) }
            }
        }

        val result = safeApiCall<Unit> { client.get("/test") }

        assertIs<Either.Success<Unit>>(result)
    }

    @Test
    fun shouldReturnClientErrorOnHttp400() = runTest {
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("", HttpStatusCode.BadRequest) }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Client, result.error)
    }

    @Test
    fun shouldReturnClientWithMessageOnHttp400WithBody() = runTest {
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("""{"message":"Invalid input"}""", HttpStatusCode.BadRequest, jsonHeaders) }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        val error = result.error
        assertIs<DataError.Network.ClientWithMessage>(error)
        assertEquals(400, error.code)
        assertEquals("Invalid input", error.message)
    }

    @Test
    fun shouldReturnServerErrorOnHttp500() = runTest {
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("", HttpStatusCode.InternalServerError) }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Server, result.error)
    }

    @Test
    fun shouldReturnClientErrorOnHttp3xx() = runTest {
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            followRedirects = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("", HttpStatusCode.MovedPermanently) }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Client, result.error)
    }

    @Test
    fun shouldReturnUnknownErrorOnUnexpectedStatusCode() = runTest {
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("", HttpStatusCode(600, "Custom")) }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Unknown, result.error)
    }

    @Test
    fun shouldThrowJsonConvertExceptionOnMalformedJson() = runTest {
        // NOTE: In Ktor 3.x, malformed JSON throws JsonConvertException
        // (extends ContentConvertException), which is NOT caught by the
        // SerializationException catch in safeApiCall. This documents
        // current behavior — a gap that should be fixed in production code.
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { respond("{not valid json", HttpStatusCode.OK, jsonHeaders) }
            }
        }

        assertFailsWith<io.ktor.serialization.JsonConvertException> {
            safeApiCall<SafeApiCallTestResponse> { client.get("/test") }
        }
    }

    // --- Exception path ---

    @Test
    fun shouldReturnConnectionErrorOnIOException() = runTest {
        val client = HttpClient(MockEngine) {
            engine { addHandler { throw IOException("network down") } }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Connection, result.error)
    }

    @Test
    fun shouldReturnTimeoutErrorOnSocketTimeoutException() = runTest {
        val client = HttpClient(MockEngine) {
            engine { addHandler { throw SocketTimeoutException("timed out") } }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Timeout, result.error)
    }

    @Test
    fun shouldReturnGuestErrorOnGuestBlockedException() = runTest {
        val client = HttpClient(MockEngine) {
            engine { addHandler { throw GuestBlockedException() } }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Guest, result.error)
    }
}
