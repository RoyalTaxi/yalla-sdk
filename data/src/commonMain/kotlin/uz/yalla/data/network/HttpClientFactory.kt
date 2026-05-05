package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.preferences.InterfacePreferences
import uz.yalla.core.preferences.PositionPreferences
import uz.yalla.core.preferences.SessionPreferences
import uz.yalla.core.session.UnauthorizedSessionEvents
import uz.yalla.data.util.platformName

private const val REQUEST_TIMEOUT_MS = 15_000L
private const val CONNECT_TIMEOUT_MS = 10_000L
private const val SOCKET_TIMEOUT_MS = 15_000L

/**
 * Creates a fully configured [HttpClient] for API communication.
 *
 * Sets up content negotiation (lenient JSON), request/connect/socket timeouts,
 * Ktor's [Auth] plugin for bearer-token attachment + 401-driven session clear,
 * the [guest mode guard][createGuestModeGuardPlugin], and dynamic headers
 * (locale, position, brand, platform).
 *
 * The platform-specific engine is resolved via [createHttpEngine].
 * Locale, guest-mode and position values are cached in
 * [StateFlow][kotlinx.coroutines.flow.StateFlow] instances and updated
 * reactively, ensuring headers always reflect current state without blocking
 * the request path. The bearer token is read from [SessionPreferences] by the
 * Auth plugin's `loadTokens` lambda; on 401 the plugin's `refreshTokens`
 * clears the session and publishes [UnauthorizedSessionEvents] (no refresh
 * endpoint exists in this codebase, so refreshing reduces to logging the user
 * out and surfacing the signal).
 *
 * ### Scope ownership
 *
 * The caller owns [scope]. Every preference-observation coroutine launched here
 * runs on that scope. Cancelling [scope] stops those coroutines cleanly; the
 * returned [HttpClient] should be [HttpClient.close]d in lockstep. Do not pass
 * a process-lifetime scope for short-lived clients — the background header
 * observers will outlive the [HttpClient] otherwise.
 *
 * @param scope caller-owned [CoroutineScope] that hosts the preference-observation
 *   coroutines; cancelling it stops header/guest-mode observers. Must outlive
 *   every request issued through the returned client.
 * @param inspektifySetup optional debug inspector plugin setup (e.g. Inspektify)
 * @param engine test seam — when non-null, the provided engine is used instead
 *   of the platform-resolved one from [createHttpEngine]. Production calls
 *   omit this parameter; tests inject `MockEngine` here.
 * @return configured [HttpClient] instance ready for use with [safeApiCall]
 * @see NetworkConfig
 * @see createHttpEngine
 * @see createGuestModeGuardPlugin
 * @see safeApiCall
 */
fun createHttpClient(
    config: NetworkConfig,
    sessionPrefs: SessionPreferences,
    interfacePrefs: InterfacePreferences,
    positionPrefs: PositionPreferences,
    scope: CoroutineScope,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)? = null,
    engine: HttpClientEngine? = null
): HttpClient {
    val localeCache = MutableStateFlow("")
    val guestModeCache = MutableStateFlow(false)
    val positionCache = MutableStateFlow(GeoPoint.Zero)

    scope.launch {
        interfacePrefs.localeType.collectLatest { localeCache.value = it.code }
    }
    scope.launch {
        sessionPrefs.isGuestMode.collectLatest { guestModeCache.value = it }
    }
    scope.launch {
        positionPrefs.lastMapPosition.collectLatest { positionCache.value = it }
    }

    return HttpClient(engine ?: createHttpEngine()) {
        inspektifySetup?.invoke(this)

        install(Logging) {
            level = LogLevel.NONE
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = sessionPrefs.accessToken.first()
                    if (token.isEmpty()) null else BearerTokens(accessToken = token, refreshToken = "")
                }
                refreshTokens {
                    // No refresh endpoint exists in this codebase. A 401 means the
                    // session is invalid; clear it and surface the signal so the UI
                    // can navigate to login. Returning null tells the Auth plugin
                    // not to retry, which lets the original request fail with 401.
                    sessionPrefs.clearSession()
                    UnauthorizedSessionEvents.publish()
                    null
                }
                sendWithoutRequest { true }
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
