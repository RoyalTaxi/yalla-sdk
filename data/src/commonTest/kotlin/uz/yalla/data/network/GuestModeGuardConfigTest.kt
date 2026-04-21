package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Covers [NetworkConfig.guestAllowedSegments] — the configurable whitelist that
 * replaces [GuestModeGuard]'s previous hardcoded list.
 */
class GuestModeGuardConfigTest {
    private val guestMode = MutableStateFlow(true)

    private fun clientFor(config: NetworkConfig) = HttpClient(MockEngine) {
        install(createGuestModeGuardPlugin(guestMode, config.guestAllowedSegments.toSet()))
        engine {
            addHandler { respond("ok", HttpStatusCode.OK) }
        }
    }

    @Test
    fun defaultConfigContainsLegacySixEndpointWhitelist() {
        val config = NetworkConfig(
            baseUrl = "https://api.example.com/",
            brandId = "brand",
            secretKey = "secret",
        )

        assertEquals(6, config.guestAllowedSegments.size)
        val expected = listOf("client", "valid", "register", "location-name", "cost", "lists")
        assertEquals(expected, config.guestAllowedSegments)
    }

    @Test
    fun defaultExportedConstantMatchesConfigDefault() {
        val config = NetworkConfig(
            baseUrl = "https://api.example.com/",
            brandId = "brand",
            secretKey = "secret",
        )

        assertEquals(DEFAULT_GUEST_ALLOWED_SEGMENTS, config.guestAllowedSegments)
    }

    @Test
    fun customWhitelistIsUsedByGuard() = runTest {
        val customConfig = NetworkConfig(
            baseUrl = "https://api.example.com/",
            brandId = "brand",
            secretKey = "secret",
            guestAllowedSegments = listOf("public"),
        )
        val client = clientFor(customConfig)

        client.get("/api/public") // passes — in custom whitelist

        assertFailsWith<GuestBlockedException> {
            client.get("/api/private") // blocked — not in custom whitelist
        }

        // Legacy default segment is NOT allowed under the custom whitelist.
        assertFailsWith<GuestBlockedException> {
            client.get("/api/client")
        }
    }

    @Test
    fun emptyWhitelistBlocksAllGuestRequests() = runTest {
        val emptyConfig = NetworkConfig(
            baseUrl = "https://api.example.com/",
            brandId = "brand",
            secretKey = "secret",
            guestAllowedSegments = emptyList(),
        )
        val client = clientFor(emptyConfig)

        assertTrue(emptyConfig.guestAllowedSegments.isEmpty())

        // Every plausible path segment blocks while guest mode is active.
        assertFailsWith<GuestBlockedException> { client.get("/anything") }
        assertFailsWith<GuestBlockedException> { client.get("/api/v1/client") }
        assertFailsWith<GuestBlockedException> { client.get("/") }
    }

    @Test
    fun emptyWhitelistStillAllowsRequestsWhenNotInGuestMode() = runTest {
        guestMode.value = false
        val emptyConfig = NetworkConfig(
            baseUrl = "https://api.example.com/",
            brandId = "brand",
            secretKey = "secret",
            guestAllowedSegments = emptyList(),
        )
        val client = clientFor(emptyConfig)

        // Empty whitelist is irrelevant once guest mode is off.
        client.get("/anything")
    }
}
