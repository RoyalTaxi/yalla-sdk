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
import uz.yalla.core.contract.AppPreferences
import uz.yalla.core.contract.StaticPreferences
import uz.yalla.core.session.UnauthorizedSessionEvents

private val localeCache = MutableStateFlow("")
private val accessTokenCache = MutableStateFlow("")

private val cacheScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

actual fun provideNetworkClient(
    config: NetworkConfig,
    appPrefs: AppPreferences,
    staticPrefs: StaticPreferences,
    inspektifySetup: (HttpClientConfig<*>.() -> Unit)?,
): HttpClient {
    cacheScope.launch {
        appPrefs.localeType.collectLatest { localeCache.value = it.code }
    }
    cacheScope.launch {
        appPrefs.accessToken.collectLatest { accessTokenCache.value = it }
    }

    return HttpClient(Android) {
        inspektifySetup?.invoke(this)

        install(Logging) {
            level = LogLevel.ALL
        }

        install(HttpCallValidator) {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    handleUnauthorized(
                        appPrefs = appPrefs,
                        staticPrefs = staticPrefs
                    )
                }
            }
            handleResponseExceptionWithRequest { cause, _ ->
                if (
                    cause is ClientRequestException &&
                    cause.response.status == HttpStatusCode.Unauthorized
                ) {
                    handleUnauthorized(
                        appPrefs = appPrefs,
                        staticPrefs = staticPrefs
                    )
                }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }

        install(createGuestModeGuardPlugin(staticPrefs))

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

        install(createDynamicHeadersPlugin(appPrefs))
    }
}

private fun createDynamicHeadersPlugin(preferences: AppPreferences) =
    createClientPlugin("DynamicHeaders") {
        onRequest { request, _ ->
            val location = preferences.lastAccessedLocation.first()

            request.headers.apply {
                set("x-position", "${location.first} ${location.second}")
            }
        }
    }

private fun handleUnauthorized(
    appPrefs: AppPreferences,
    staticPrefs: StaticPreferences
) {
    val token = accessTokenCache.value
    if (token.isEmpty()) return
    if (!accessTokenCache.compareAndSet(token, "")) return

    appPrefs.performLogout()
    staticPrefs.performLogout()
    localeCache.value = staticPrefs.localeType.code
    UnauthorizedSessionEvents.publish()
}
