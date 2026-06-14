package uz.yalla.network

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Output-based regression net for the live stale-locale bug: the `lang` header used to be set in
 * `defaultRequest { }`, which captures `localeCache.value` exactly once at client construction —
 * when the cache still holds its initial empty string. A user who switched language mid-session
 * kept shipping the stale (empty) `lang` until the app restarted.
 *
 * Driving the client through a [MockEngine] lets us assert the header that actually goes on the
 * wire: the first request reflects the current cache, and a subsequent request — issued after the
 * locale flow emits a new value — carries the NEW `lang` rather than the captured-once original.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DynamicLangHeaderTest {
    private fun config() =
        NetworkConfig(
            baseUrl = "https://api.test.invalid/",
            brandId = "brand",
            secretKey = "secret"
        )

    /**
     * The SDK mirrors each source flow into an internal cache via a `collectLatest` collector on a
     * background coroutine. After emitting a new value we must let that collector run before the
     * next request reads the cache: `advanceUntilIdle` drains the relaunched collector task, and the
     * trailing `yield` hands the dispatcher back to the just-resumed collector so its write lands
     * before we proceed. Deterministic under [UnconfinedTestDispatcher] — no wall-clock waiting.
     */
    private suspend fun TestScope.settleCaches() {
        advanceUntilIdle()
        yield()
    }

    @Test
    fun langHeader_followsLocaleFlow_acrossRequests() =
        runTest(UnconfinedTestDispatcher()) {
            val sentLangs = mutableListOf<String?>()
            val engine =
                MockEngine { request ->
                    sentLangs += request.headers["lang"]
                    respondOk()
                }

            val locale = MutableStateFlow("uz")

            val client =
                createHttpClient(
                    config = config(),
                    accessToken = { null },
                    locale = locale,
                    guestMode = flowOf(false),
                    position = flowOf(GeoPoint.Zero),
                    onUnauthorized = {},
                    scope = backgroundScope,
                    engine = engine
                )

            val first: HttpResponse = client.get("ping")

            locale.value = "ru"
            settleCaches()
            val second: HttpResponse = client.get("ping")

            check(first.status.value in 200..299 && second.status.value in 200..299)
            assertEquals(listOf<String?>("uz", "ru"), sentLangs)
        }

    @Test
    fun langHeader_isNotCapturedAtConstruction_whenCacheStartsEmpty() =
        runTest(UnconfinedTestDispatcher()) {
            val sentLangs = mutableListOf<String?>()
            val engine =
                MockEngine { request ->
                    sentLangs += request.headers["lang"]
                    respondOk()
                }

            // Locale resolves only after the client is built — mirrors a session that learns the
            // language late. The old `defaultRequest` capture would freeze the empty initial value.
            val locale = MutableStateFlow("")

            val client =
                createHttpClient(
                    config = config(),
                    accessToken = { null },
                    locale = locale,
                    guestMode = flowOf(false),
                    position = flowOf(GeoPoint.Zero),
                    onUnauthorized = {},
                    scope = backgroundScope,
                    engine = engine
                )

            locale.value = "en"
            settleCaches()
            val response: HttpResponse = client.get("ping")

            check(response.status.value in 200..299)
            assertEquals(listOf<String?>("en"), sentLangs)
        }
}
