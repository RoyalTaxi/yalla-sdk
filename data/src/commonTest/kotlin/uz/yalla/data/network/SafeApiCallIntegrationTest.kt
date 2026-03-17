package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import uz.yalla.core.error.DataError
import uz.yalla.core.result.Either
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SafeApiCallIntegrationTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    @Test
    fun shouldRetryAndSucceedOnTransientFailure() = runTest {
        var callCount = 0
        val client = HttpClient(MockEngine) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler {
                    callCount++
                    if (callCount < 2) throw IOException("transient")
                    respond("""{"id":1,"name":"recovered"}""", HttpStatusCode.OK, jsonHeaders)
                }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse>(isIdempotent = true) { client.get("/test") }

        assertIs<Either.Success<SafeApiCallTestResponse>>(result)
        assertEquals("recovered", result.data.name)
        assertEquals(2, callCount)
    }

    @Test
    fun shouldReturnFailureWhenRetriesExhausted() = runTest {
        var callCount = 0
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    callCount++
                    throw IOException("persistent failure")
                }
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse>(isIdempotent = true) { client.get("/test") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Connection, result.error)
        assertEquals(3, callCount)
    }
}
