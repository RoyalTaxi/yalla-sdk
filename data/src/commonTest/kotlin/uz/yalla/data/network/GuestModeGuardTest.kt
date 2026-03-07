package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class GuestModeGuardTest {
    private val guestMode = MutableStateFlow(false)

    private fun createClient(allowedSegments: Set<String> = setOf("client", "valid")) =
        HttpClient(MockEngine) {
            install(createGuestModeGuardPlugin(guestMode, allowedSegments))
            engine {
                addHandler { respond("ok", HttpStatusCode.OK) }
            }
        }

    @Test
    fun shouldAllowAllRequestsWhenNotInGuestMode() = runTest {
        guestMode.value = false
        val client = createClient()

        val response = client.get("/api/restricted-endpoint")

        assertIs<Unit>(Unit) // no exception thrown
    }

    @Test
    fun shouldAllowWhitelistedSegmentsInGuestMode() = runTest {
        guestMode.value = true
        val client = createClient()

        client.get("/api/client")
        client.get("/api/v1/valid")
        // no exception — both are allowed
    }

    @Test
    fun shouldBlockNonWhitelistedSegmentsInGuestMode() = runTest {
        guestMode.value = true
        val client = createClient()

        assertFailsWith<GuestBlockedException> {
            client.get("/api/orders")
        }
    }

    @Test
    fun shouldMatchOnlyLastPathSegment() = runTest {
        guestMode.value = true
        val client = createClient(allowedSegments = setOf("allowed"))

        assertFailsWith<GuestBlockedException> {
            client.get("/allowed/not-allowed")
        }
    }

    @Test
    fun shouldHandleTrailingSlash() = runTest {
        guestMode.value = true
        val client = createClient(allowedSegments = setOf("client"))

        client.get("/api/client/")
        // no exception — trailing slash is trimmed
    }
}
