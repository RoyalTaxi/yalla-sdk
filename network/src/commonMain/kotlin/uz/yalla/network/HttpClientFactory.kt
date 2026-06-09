package uz.yalla.network

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uz.yalla.core.geo.GeoPoint

private const val REQUEST_TIMEOUT_MS = 15_000L
private const val CONNECT_TIMEOUT_MS = 10_000L
private const val SOCKET_TIMEOUT_MS = 15_000L

fun createHttpClient(
    config: NetworkConfig,
    accessToken: suspend () -> String?,
    locale: Flow<String>,
    guestMode: Flow<Boolean>,
    position: Flow<GeoPoint>,
    onUnauthorized: suspend () -> Unit,
    scope: CoroutineScope,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)? = null,
    engine: HttpClientEngine? = null,
    loggingEnabled: Boolean = false
): HttpClient {
    val localeCache = MutableStateFlow("")
    val guestModeCache = MutableStateFlow(false)
    val positionCache = MutableStateFlow(GeoPoint.Zero)

    scope.launch { locale.collectLatest { localeCache.value = it } }
    scope.launch { guestMode.collectLatest { guestModeCache.value = it } }
    scope.launch { position.collectLatest { positionCache.value = it } }

    return HttpClient(engine ?: createHttpEngine()) {
        inspektifySetup?.invoke(this)

        if (loggingEnabled) {
            install(Logging) {
                level = LogLevel.BODY
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = accessToken()
                    if (token.isNullOrEmpty()) null else BearerTokens(accessToken = token, refreshToken = "")
                }
                refreshTokens {
                    onUnauthorized()
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
