package uz.yalla.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uz.yalla.core.contract.preferences.InterfacePreferences
import uz.yalla.core.contract.preferences.PositionPreferences
import uz.yalla.core.contract.preferences.SessionPreferences
import uz.yalla.core.session.UnauthorizedSessionEvents

private val localeCache = MutableStateFlow("")
private val accessTokenCache = MutableStateFlow("")
private val guestModeCache = MutableStateFlow(false)

private val cacheScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
private const val BEARER_PREFIX = "Bearer "

actual fun provideNetworkClient(
    config: NetworkConfig,
    sessionPrefs: SessionPreferences,
    interfacePrefs: InterfacePreferences,
    positionPrefs: PositionPreferences,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)?,
): HttpClient {
    cacheScope.launch {
        interfacePrefs.localeType.collectLatest { localeCache.value = it.code }
    }
    cacheScope.launch {
        sessionPrefs.accessToken.collectLatest { accessTokenCache.value = it }
    }
    cacheScope.launch {
        sessionPrefs.isGuestMode.collectLatest { guestModeCache.value = it }
    }

    return HttpClient(Android) {
        inspektifySetup?.invoke(this)

        install(Logging) {
            level = LogLevel.ALL
        }

        install(HttpCallValidator) {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    val requestToken = response.call.request.headers[HttpHeaders.Authorization].extractBearerToken()
                    handleUnauthorized(sessionPrefs, requestToken)
                }
            }
            handleResponseExceptionWithRequest { cause, request ->
                if (
                    cause is ClientRequestException &&
                    cause.response.status == HttpStatusCode.Unauthorized
                ) {
                    val requestToken = request.headers[HttpHeaders.Authorization].extractBearerToken()
                    handleUnauthorized(sessionPrefs, requestToken)
                }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }

        install(createGuestModeGuardPlugin(guestModeCache))

        defaultRequest {
            url(config.baseUrl)
            header("lang", localeCache.value)
            header("brand-id", config.brandId)
            header("User-Agent-OS", config.userAgentOS.ifEmpty { "android" })
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

        install(createDynamicHeadersPlugin(positionPrefs))
    }
}

private fun createDynamicHeadersPlugin(positionPrefs: PositionPreferences) =
    createClientPlugin("DynamicHeaders") {
        onRequest { request, _ ->
            val location = positionPrefs.lastMapPosition.first()

            request.headers.apply {
                set("x-position", "${location.lat} ${location.lng}")
            }
        }
    }

private fun handleUnauthorized(
    sessionPrefs: SessionPreferences,
    requestToken: String?
) {
    val currentToken = accessTokenCache.value
    if (currentToken.isEmpty()) return
    if (requestToken.isNullOrEmpty()) return
    if (requestToken != currentToken) return
    if (!accessTokenCache.compareAndSet(currentToken, "")) return

    sessionPrefs.performLogout()
    localeCache.value = ""
    UnauthorizedSessionEvents.publish()
}

private fun String?.extractBearerToken(): String? {
    val value = this?.trim().orEmpty()
    if (!value.startsWith(BEARER_PREFIX, ignoreCase = true)) return null
    if (value.length <= BEARER_PREFIX.length) return null
    return value.substring(BEARER_PREFIX.length).trim().ifEmpty { null }
}
