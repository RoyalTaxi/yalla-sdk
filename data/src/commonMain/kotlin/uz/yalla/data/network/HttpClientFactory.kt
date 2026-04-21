package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.contract.preferences.PositionPreferences
import uz.yalla.core.contract.preferences.SessionPreferences
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.session.UnauthorizedSessionEvents
import uz.yalla.data.util.platformName

private const val BEARER_PREFIX = "Bearer "
private const val REQUEST_TIMEOUT_MS = 15_000L
private const val CONNECT_TIMEOUT_MS = 10_000L
private const val SOCKET_TIMEOUT_MS = 15_000L

/**
 * Creates a fully configured [HttpClient] for API communication.
 *
 * Sets up content negotiation (lenient JSON), request/connect/socket timeouts,
 * automatic 401 handling (clears session and publishes
 * [UnauthorizedSessionEvents]), [guest mode guard][createGuestModeGuardPlugin],
 * and dynamic headers (locale, position, brand, platform).
 *
 * The platform-specific engine is resolved via [createHttpEngine].
 * Preference values are cached in [StateFlow][kotlinx.coroutines.flow.StateFlow]
 * instances and updated reactively, ensuring headers always reflect current state
 * without blocking the request path.
 *
 * ### Scope ownership
 *
 * The caller owns [scope]. Every preference-observation coroutine launched here
 * runs on that scope. Cancelling [scope] stops those coroutines cleanly; the
 * returned [HttpClient] should be [HttpClient.close]d in lockstep. Do not pass
 * a process-lifetime scope for short-lived clients — the background header
 * observers will outlive the [HttpClient] otherwise. See ADR-011 in
 * `docs/06-DECISIONS.md` for the rationale.
 *
 * @param config network configuration (base URL, brand, secret)
 * @param sessionPrefs session state (token, guest mode)
 * @param interfacePrefs interface state (locale)
 * @param positionPrefs position state (last known location)
 * @param scope caller-owned [CoroutineScope] that hosts the preference-observation
 *   coroutines; cancelling it stops header/guest-mode observers. Must outlive
 *   every request issued through the returned client.
 * @param inspektifySetup optional debug inspector plugin setup (e.g. Inspektify)
 * @return configured [HttpClient] instance ready for use with [safeApiCall]
 * @see NetworkConfig
 * @see createHttpEngine
 * @see createGuestModeGuardPlugin
 * @see safeApiCall
 * @since 0.0.5
 */
fun createHttpClient(
    config: NetworkConfig,
    sessionPrefs: SessionPreferences,
    interfacePrefs: InterfacePreferences,
    positionPrefs: PositionPreferences,
    scope: CoroutineScope,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)? = null,
): HttpClient {
    val localeCache = MutableStateFlow("")
    val accessTokenCache = MutableStateFlow("")
    val guestModeCache = MutableStateFlow(false)
    val positionCache = MutableStateFlow(GeoPoint.Zero)

    scope.launch {
        interfacePrefs.localeType.collectLatest { localeCache.value = it.code }
    }
    scope.launch {
        sessionPrefs.accessToken.collectLatest { accessTokenCache.value = it }
    }
    scope.launch {
        sessionPrefs.isGuestMode.collectLatest { guestModeCache.value = it }
    }
    scope.launch {
        positionPrefs.lastMapPosition.collectLatest { positionCache.value = it }
    }

    return HttpClient(createHttpEngine()) {
        inspektifySetup?.invoke(this)

        install(Logging) {
            level = LogLevel.NONE
        }

        install(HttpCallValidator) {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    val requestToken =
                        response.call.request
                            .headers[HttpHeaders.Authorization]
                            .extractBearerToken()
                    handleUnauthorized(sessionPrefs, accessTokenCache, requestToken)
                }
            }
            handleResponseExceptionWithRequest { cause, request ->
                if (
                    cause is ClientRequestException &&
                    cause.response.status == HttpStatusCode.Unauthorized
                ) {
                    val requestToken =
                        request.headers[HttpHeaders.Authorization]
                            .extractBearerToken()
                    handleUnauthorized(sessionPrefs, accessTokenCache, requestToken)
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
            header("User-Agent-OS", platformName)
            header("Content-Type", "application/json")
            header("Device-Mode", config.deviceMode)
            header("Device", config.deviceType)
            header("secret-key", config.secretKey)
            header("Authorization", BEARER_PREFIX + accessTokenCache.value)
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
    }
}

private fun handleUnauthorized(
    sessionPrefs: SessionPreferences,
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

private fun String?.extractBearerToken(): String? {
    val value = this?.trim().orEmpty()
    if (!value.startsWith(BEARER_PREFIX, ignoreCase = true)) return null
    if (value.length <= BEARER_PREFIX.length) return null
    return value.substring(BEARER_PREFIX.length).trim().ifEmpty { null }
}
