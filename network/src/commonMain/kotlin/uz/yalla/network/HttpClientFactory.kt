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
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.encodedPath
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

internal val SENSITIVE_HEADERS: List<String> = listOf("Authorization", "secret-key", "x-position")

internal val SENSITIVE_BODY_PATHS: List<String> = listOf("client", "valid", "register")

public fun createHttpClient(
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

    val client =
        HttpClient(engine ?: createHttpEngine(config.certificatePins)) {
            inspektifySetup?.invoke(this)

            if (loggingEnabled) {
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.BODY
                    SENSITIVE_HEADERS.forEach { name -> sanitizeHeader { it.equals(name, ignoreCase = true) } }
                    filter { request ->
                        SENSITIVE_BODY_PATHS.none { isGuestAllowedPath(request.url.encodedPath, setOf(it)) }
                    }
                }
            }

            install(Auth) {
                bearer {
                    cacheTokens = false
                    loadTokens {
                        val token = accessToken()
                        if (token.isNullOrBlank()) null else BearerTokens(accessToken = token, refreshToken = "")
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

            install(createGuestModeGuardPlugin(guestModeCache, config.guestAllowedPaths.toSet()))

            defaultRequest {
                url(config.baseUrl)
                val lang = localeCache.value
                if (lang.isNotBlank()) header("lang", lang)
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
                        if (location != GeoPoint.Zero) {
                            request.headers.set("x-position", formatPosition(location))
                        }
                    }
                }
            )
        }

    client.launch { locale.collectLatest { localeCache.value = it } }
    client.launch { guestMode.collectLatest { guestModeCache.value = it } }
    client.launch { position.collectLatest { positionCache.value = it } }

    return client
}
