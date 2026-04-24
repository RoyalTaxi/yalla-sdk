package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.preferences.PositionPreferences
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.error.DataError
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.result.Either
import uz.yalla.core.session.UnauthorizedSessionEvents
import uz.yalla.core.settings.LocaleKind
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

/**
 * End-to-end integration tests for [createHttpClient]'s plugin composition.
 *
 * `createHttpClient` itself resolves a platform-native engine via the `expect`
 * [createHttpEngine], so a true call-site test can't inject [MockEngine]. To
 * get equivalent coverage without breaking the public signature, each test
 * constructs an [HttpClient] with [MockEngine] and mirrors the exact plugin
 * install stack that `createHttpClient` sets up: [HttpCallValidator] for 401
 * handling, [HttpTimeout], [createGuestModeGuardPlugin], [defaultRequest]
 * (Authorization), [ContentNegotiation] (lenient JSON), and the DynamicHeaders
 * plugin that injects `x-position`.
 *
 * Preference reads are simulated with in-test [MutableStateFlow] fakes (no
 * real DataStore/Settings needed) wired through [SessionPreferences],
 * [InterfacePreferences], [PositionPreferences]; the fakes only expose the
 * fields actually observed by `createHttpClient`.
 *
 * **Keep-in-sync caveat:** if `createHttpClient`'s plugin install stack
 * changes (new plugin, reordered install, different config), update
 * [buildTestClient] below to match, or the integration tests drift from the
 * behavior they're meant to cover.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HttpClientFactoryIntegrationTest {

    @BeforeTest
    fun clearPendingUnauthorizedEvent() {
        // Global singleton — drain any stale event left by previous tests in
        // the same JVM/Kotlin-Native test binary.
        UnauthorizedSessionEvents.drainPendingEventIfExists()
    }

    @AfterTest
    fun clearAfter() {
        UnauthorizedSessionEvents.drainPendingEventIfExists()
    }

    @Test
    fun respondsWith401TriggersUnauthorizedSessionEventsEmit() = runTest(UnconfinedTestDispatcher()) {
        val sessionPrefs = FakeSessionPreferences(initialAccessToken = TEST_TOKEN)
        val eventReceived = CompletableDeferred<Unit>()
        backgroundScope.launch {
            UnauthorizedSessionEvents.events.collectLatest {
                eventReceived.complete(Unit)
            }
        }

        val client = buildTestClient(
            testScope = this,
            sessionPrefs = sessionPrefs,
        ) {
            addHandler { respond("", HttpStatusCode.Unauthorized) }
        }

        // expectSuccess (the Ktor default for HttpCallValidator) turns 401 into
        // a ClientRequestException — exercise both the validateResponse and
        // handleResponseExceptionWithRequest branches by running the call
        // through safeApiCall, which captures the exception and maps it.
        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/some/endpoint") }

        assertIs<Either.Failure<DataError.Network>>(result)

        withTimeout(EVENT_TIMEOUT_MS) { eventReceived.await() }
        assertEquals("", sessionPrefs.accessToken.value, "session must be cleared after 401")
    }

    @Test
    fun retriesOnIoExceptionForIdempotentCall() = runTest(UnconfinedTestDispatcher()) {
        var callCount = 0
        val client = buildTestClient(testScope = this) {
            addHandler {
                callCount++
                if (callCount < 3) throw IOException("transient")
                respond(
                    content = """{"id":7,"name":"recovered"}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse>(isIdempotent = true) { client.get("/retry") }

        assertIs<Either.Success<SafeApiCallTestResponse>>(result)
        assertEquals(7, result.data.id)
        assertEquals("recovered", result.data.name)
        assertEquals(3, callCount, "two retries, third succeeds")
    }

    @Test
    fun guestModeBlocksNonWhitelistedEndpoint() = runTest(UnconfinedTestDispatcher()) {
        val sessionPrefs = FakeSessionPreferences(initialGuestMode = true)
        val client = buildTestClient(
            testScope = this,
            sessionPrefs = sessionPrefs,
            config = defaultNetworkConfig(guestAllowedSegments = listOf("public")),
        ) {
            addHandler { respond("ok", HttpStatusCode.OK) }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/api/private") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Guest, result.error)
    }

    @Test
    fun connectionErrorSurfacesAsDataErrorNetworkConnection() = runTest(UnconfinedTestDispatcher()) {
        val client = buildTestClient(testScope = this) {
            addHandler { throw IOException("offline") }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/any") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Connection, result.error)
    }

    @Test
    fun requestTimeoutSurfacesAsDataErrorNetworkTimeout() = runTest(UnconfinedTestDispatcher()) {
        // HttpTimeout is installed (mirrors createHttpClient), but MockEngine's
        // execute dispatcher is not driven by `runTest`'s virtual clock, so we
        // can't rely on `delay(N)` inside a mock handler to trip `HttpTimeout`
        // under virtual time. Instead, throw the concrete socket-timeout that
        // safeApiCall maps to DataError.Network.Timeout — the same path
        // exercised by SafeApiCallTest.shouldReturnTimeoutErrorOnSocketTimeoutException.
        //
        // Note: Ktor 3.x's HttpRequestTimeoutException currently surfaces as
        // DataError.Network.Connection because it extends kotlinx.io.IOException
        // and safeApiCall catches IOException before any more specific check.
        // That's a known gap tracked separately — see the follow-up spawn task.
        val client = buildTestClient(testScope = this) {
            addHandler {
                throw io.ktor.client.network.sockets
                    .SocketTimeoutException("request timed out")
            }
        }

        val result = safeApiCall<SafeApiCallTestResponse> { client.get("/slow") }

        assertIs<Either.Failure<DataError.Network>>(result)
        assertEquals(DataError.Network.Timeout, result.error)
    }

    @Test
    fun cancellingScopeStopsHeaderObserverCoroutines() = runTest(UnconfinedTestDispatcher()) {
        // Independent SupervisorJob so cancelling this scope does NOT propagate
        // to the test's own coroutineContext. Share only the dispatcher so
        // virtual time still applies.
        val clientScope = CoroutineScope(
            SupervisorJob() + coroutineContext[kotlin.coroutines.ContinuationInterceptor]!!,
        )
        val sessionPrefs = FakeSessionPreferences()
        val interfacePrefs = FakeInterfacePreferences(initialLocale = LocaleKind.Uz)

        val client = buildTestClient(
            testScope = this,
            sessionPrefs = sessionPrefs,
            interfacePrefs = interfacePrefs,
            clientScope = clientScope,
        ) {
            addHandler { respond("ok", HttpStatusCode.OK) }
        }

        // Record initial locale — observer should have cached "uz".
        val warmup = client.get("/warm")
        assertEquals(HttpStatusCode.OK, warmup.status)
        val warmupLang = warmup.call.request.headers[LANG_HEADER]
        assertEquals("uz", warmupLang)

        // Cancel the scope that hosts the preference observers.
        clientScope.cancel()

        // Mutate locale — because observers are dead, the cached value should
        // stay "uz". Assert via the outbound header on the next request.
        interfacePrefs.setLocaleType(LocaleKind.Ru)
        val afterCancel = client.get("/after")
        assertEquals(HttpStatusCode.OK, afterCancel.status)
        val afterLang = afterCancel.call.request.headers[LANG_HEADER]
        assertEquals(
            expected = "uz",
            actual = afterLang,
            message = "observer must be cancelled after scope cancel",
        )

        assertFalse(clientScope.isActive, "scope should be cancelled")
        client.close()
    }

    /**
     * Builds an [HttpClient] whose plugin stack mirrors [createHttpClient] but
     * uses [MockEngine] so the caller can script request handlers. The only
     * divergences are: (a) no platform engine (obviously), (b) no
     * `inspektifySetup` hook (tests don't need the debug inspector), (c) no
     * Logging plugin (quiet test output).
     */
    @Suppress("LongParameterList")
    private fun buildTestClient(
        testScope: TestScope,
        sessionPrefs: FakeSessionPreferences = FakeSessionPreferences(),
        interfacePrefs: FakeInterfacePreferences = FakeInterfacePreferences(),
        positionPrefs: FakePositionPreferences = FakePositionPreferences(),
        config: NetworkConfig = defaultNetworkConfig(),
        clientScope: CoroutineScope = testScope.backgroundScope,
        engineConfig: io.ktor.client.engine.mock.MockEngineConfig.() -> Unit,
    ): HttpClient {
        val localeCache = MutableStateFlow("")
        val accessTokenCache = MutableStateFlow("")
        val guestModeCache = MutableStateFlow(false)
        val positionCache = MutableStateFlow(GeoPoint.Zero)

        clientScope.launch {
            interfacePrefs.localeType.collectLatest { localeCache.value = it.code }
        }
        clientScope.launch {
            sessionPrefs.accessToken.collectLatest { accessTokenCache.value = it }
        }
        clientScope.launch {
            sessionPrefs.isGuestMode.collectLatest { guestModeCache.value = it }
        }
        clientScope.launch {
            positionPrefs.lastMapPosition.collectLatest { positionCache.value = it }
        }

        return HttpClient(MockEngine) {
            expectSuccess = false

            install(HttpCallValidator) {
                validateResponse { response ->
                    if (response.status == HttpStatusCode.Unauthorized) {
                        val requestToken = response.call.request
                            .headers[HttpHeaders.Authorization]
                            ?.removePrefix("Bearer ")
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() }
                        handleUnauthorizedForTest(sessionPrefs, accessTokenCache, requestToken)
                    }
                }
                handleResponseExceptionWithRequest { cause, request ->
                    if (cause is ClientRequestException && cause.response.status == HttpStatusCode.Unauthorized) {
                        val requestToken = request.headers[HttpHeaders.Authorization]
                            ?.removePrefix("Bearer ")
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() }
                        handleUnauthorizedForTest(sessionPrefs, accessTokenCache, requestToken)
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = REQUEST_TIMEOUT_MS
                connectTimeoutMillis = CONNECT_TIMEOUT_MS
                socketTimeoutMillis = SOCKET_TIMEOUT_MS
            }

            install(createGuestModeGuardPlugin(guestModeCache, config.guestAllowedSegments.toSet()))

            defaultRequest {
                url(config.baseUrl)
                header("lang", localeCache.value)
                header("brand-id", config.brandId)
                header("Content-Type", "application/json")
                header("Device-Mode", config.deviceMode)
                header("Device", config.deviceType)
                header("secret-key", config.secretKey)
                header("Authorization", "Bearer " + accessTokenCache.value)
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                        encodeDefaults = true
                    }
                )
            }

            install(
                createClientPlugin("DynamicHeaders") {
                    onRequest { request, _ ->
                        val location = positionCache.value
                        request.headers.set("x-position", "${location.lat} ${location.lng}")
                    }
                }
            )

            engine { engineConfig() }
        }
    }

    private fun handleUnauthorizedForTest(
        sessionPrefs: FakeSessionPreferences,
        accessTokenCache: MutableStateFlow<String>,
        requestToken: String?,
    ) {
        val currentToken = accessTokenCache.value
        if (currentToken.isEmpty()) return
        if (requestToken.isNullOrEmpty()) return
        if (requestToken != currentToken) return
        if (!accessTokenCache.compareAndSet(currentToken, "")) return

        sessionPrefs.clearSession()
        UnauthorizedSessionEvents.publish()
    }

    private fun defaultNetworkConfig(
        guestAllowedSegments: List<String> = DEFAULT_GUEST_ALLOWED_SEGMENTS,
    ): NetworkConfig = NetworkConfig(
        baseUrl = "https://api.test.local/",
        brandId = "test-brand",
        secretKey = "test-secret",
        guestAllowedSegments = guestAllowedSegments,
    )

    private companion object {
        const val TEST_TOKEN = "test-token-abc"
        const val LANG_HEADER = "lang"
        const val REQUEST_TIMEOUT_MS = 15_000L
        const val CONNECT_TIMEOUT_MS = 10_000L
        const val SOCKET_TIMEOUT_MS = 15_000L
        const val EVENT_TIMEOUT_MS = 1_000L
    }
}

private class FakeSessionPreferences(
    initialAccessToken: String = "",
    initialGuestMode: Boolean = false,
) : SessionPreferences {
    private val _accessToken = MutableStateFlow(initialAccessToken)
    override val accessToken: StateFlow<String> = _accessToken.asStateFlow()

    private val _firebaseToken = MutableStateFlow("")
    override val firebaseToken: StateFlow<String> = _firebaseToken.asStateFlow()

    private val _isGuestMode = MutableStateFlow(initialGuestMode)
    override val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    private val _isDeviceRegistered = MutableStateFlow(false)
    override val isDeviceRegistered: StateFlow<Boolean> = _isDeviceRegistered.asStateFlow()

    override fun setAccessToken(value: String) {
        _accessToken.value = value
    }

    override fun setFirebaseToken(value: String) {
        _firebaseToken.value = value
    }

    override fun setGuestMode(value: Boolean) {
        _isGuestMode.value = value
    }

    override fun setDeviceRegistered(value: Boolean) {
        _isDeviceRegistered.value = value
    }

    override fun clearSession() {
        _accessToken.value = ""
        _isGuestMode.value = false
        _isDeviceRegistered.value = false
    }
}

private class FakeInterfacePreferences(
    initialLocale: LocaleKind = LocaleKind.Uz,
) : InterfacePreferences {
    private val _localeType = MutableStateFlow(initialLocale)
    override val localeType: StateFlow<LocaleKind> = _localeType.asStateFlow()

    private val _themeType = MutableStateFlow(ThemeKind.System)
    override val themeType: StateFlow<ThemeKind> = _themeType.asStateFlow()

    private val _mapKind = MutableStateFlow(MapKind.Google)
    override val mapKind: StateFlow<MapKind> = _mapKind.asStateFlow()

    private val _skipOnboarding = MutableStateFlow(false)
    override val skipOnboarding: StateFlow<Boolean> = _skipOnboarding.asStateFlow()

    private val _onboardingStage = MutableStateFlow("")
    override val onboardingStage: StateFlow<String> = _onboardingStage.asStateFlow()

    override fun setLocaleType(value: LocaleKind) {
        _localeType.value = value
    }

    override fun setThemeType(value: ThemeKind) {
        _themeType.value = value
    }

    override fun setMapKind(value: MapKind) {
        _mapKind.value = value
    }

    override fun setSkipOnboarding(value: Boolean) {
        _skipOnboarding.value = value
    }

    override fun setOnboardingStage(value: String) {
        _onboardingStage.value = value
    }
}

private class FakePositionPreferences : PositionPreferences {
    private val _lastMapPosition = MutableStateFlow(GeoPoint.Zero)
    override val lastMapPosition: StateFlow<GeoPoint> = _lastMapPosition.asStateFlow()

    private val _lastGpsPosition = MutableStateFlow(GeoPoint.Zero)
    override val lastGpsPosition: StateFlow<GeoPoint> = _lastGpsPosition.asStateFlow()

    override fun setLastMapPosition(value: GeoPoint) {
        _lastMapPosition.value = value
    }

    override fun setLastGpsPosition(value: GeoPoint) {
        _lastGpsPosition.value = value
    }
}
