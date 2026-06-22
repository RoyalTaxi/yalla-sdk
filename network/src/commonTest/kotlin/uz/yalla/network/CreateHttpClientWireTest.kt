package uz.yalla.network

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.result.Either
import uz.yalla.network.error.DataError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateHttpClientWireTest {
    private val captured = mutableListOf<HttpRequestData>()

    private fun mockEngine(status: HttpStatusCode = HttpStatusCode.OK) =
        MockEngine { request ->
            captured += request
            respond("""{"result":"ok"}""", status, headersOf(HttpHeaders.ContentType, "application/json"))
        }

    private fun config() = NetworkConfig(baseUrl = "https://example.test/", brandId = "2", secretKey = "k")

    private fun buildClient(
        accessToken: suspend () -> String?,
        onUnauthorized: suspend () -> Unit = {},
        locale: Flow<String> = emptyFlow(),
        position: Flow<GeoPoint> = emptyFlow(),
        status: HttpStatusCode = HttpStatusCode.OK
    ) = createHttpClient(
        config = config(),
        accessToken = accessToken,
        locale = locale,
        guestMode = MutableStateFlow(false),
        position = position,
        onUnauthorized = onUnauthorized,
        scope = CoroutineScope(Dispatchers.Unconfined),
        engine = mockEngine(status)
    )

    @Test
    fun blankTokenSendsNoAuthorizationHeader() =
        runTest {
            val client = buildClient(accessToken = { "   " })
            client.get("x")
            assertNull(captured.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun presentTokenSendsBearerHeader() =
        runTest {
            val client = buildClient(accessToken = { "tok-123" })
            client.get("x")
            assertEquals("Bearer tok-123", captured.single().headers[HttpHeaders.Authorization])
        }

    @Test
    fun tokenIsReReadPerRequestSoLoginIsPickedUpWithoutCacheInvalidation() =
        runTest {
            var token: String? = null
            val client = buildClient(accessToken = { token })
            client.get("x")
            token = "fresh-after-login"
            client.get("x")
            assertNull(captured[0].headers[HttpHeaders.Authorization])
            assertEquals("Bearer fresh-after-login", captured[1].headers[HttpHeaders.Authorization])
        }

    @Test
    fun unsetLocaleOmitsLangHeaderRatherThanSendingEmpty() =
        runTest {
            val client = buildClient(accessToken = { null })
            client.get("x")
            assertNull(captured.single().headers["lang"])
        }

    @Test
    fun unsetPositionOmitsXPositionRatherThanSendingZeroZero() =
        runTest {
            val client = buildClient(accessToken = { null })
            client.get("x")
            assertNull(captured.single().headers["x-position"])
        }

    @Test
    fun unauthorized401FiresLogoutHookAndSurfacesUnauthorized() =
        runTest {
            var loggedOut = false
            val client =
                buildClient(
                    accessToken = { "expired" },
                    onUnauthorized = { loggedOut = true },
                    status = HttpStatusCode.Unauthorized
                )
            val result = safeApiCall<ApiResponse<String>> { client.get("x") }
            assertTrue(loggedOut)
            assertEquals(Either.Failure(DataError.Network.Unauthorized), result)
        }

    @Test
    fun staticHeadersAreAlwaysAttached() =
        runTest {
            val client = buildClient(accessToken = { null })
            client.get("x")
            val headers = captured.single().headers
            assertEquals("2", headers["brand-id"])
            assertEquals("k", headers["secret-key"])
            assertFalse(headers["User-Agent-OS"].isNullOrBlank())
        }
}
