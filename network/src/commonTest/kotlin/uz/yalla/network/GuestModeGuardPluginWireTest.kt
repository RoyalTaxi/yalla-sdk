package uz.yalla.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import uz.yalla.core.result.Either
import uz.yalla.network.error.DataError
import kotlin.test.Test
import kotlin.test.assertEquals

class GuestModeGuardPluginWireTest {
    private val guestMode = MutableStateFlow(true)

    private fun client(): HttpClient {
        val engine = MockEngine { respond("""{"result":"ok"}""", HttpStatusCode.OK, jsonHeaders) }
        return HttpClient(engine) {
            install(createGuestModeGuardPlugin(guestMode, setOf("client")))
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }

    @Test
    fun guestRequestToDisallowedPathIsBlocked() =
        runTest {
            val result: Either<DataError.Network, ApiResponse<String>> =
                safeApiCall { client().get("order") }
            assertEquals(Either.Failure(DataError.Network.Guest), result)
        }

    @Test
    fun guestRequestToAllowedPathPasses() =
        runTest {
            val result: Either<DataError.Network, ApiResponse<String>> =
                safeApiCall { client().get("client") }
            assertEquals(Either.Success(ApiResponse("ok")), result)
        }

    @Test
    fun flippingGuestModeOffUnblocksThePreviouslyBlockedPath() =
        runTest {
            val client = client()
            guestMode.value = true
            val blocked: Either<DataError.Network, ApiResponse<String>> =
                safeApiCall { client.get("order") }
            assertEquals(Either.Failure(DataError.Network.Guest), blocked)

            guestMode.value = false
            val allowed: Either<DataError.Network, ApiResponse<String>> =
                safeApiCall { client.get("order") }
            assertEquals(Either.Success(ApiResponse("ok")), allowed)
        }

    private companion object {
        val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    }
}
