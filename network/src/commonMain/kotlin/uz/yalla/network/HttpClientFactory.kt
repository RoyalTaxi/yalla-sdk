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

/**
 * Request headers whose value must never be written to a log sink: the bearer token, the static
 * `secret-key`, and the precise-location `x-position`. The [Logging] plugin redacts these
 * unconditionally so credentials and PII cannot leak via logcat/Console.app/crash collectors.
 */
internal val SENSITIVE_HEADERS: List<String> = listOf("Authorization", "secret-key", "x-position")

/**
 * Endpoint paths whose request/response bodies carry secrets (OTP, phone number, freshly minted
 * tokens). Whole calls to these paths are excluded from body logging — [sanitizeHeader] only redacts
 * header values, not bodies. Matched as whole-segment suffixes, like the guest allowlist.
 */
internal val SENSITIVE_BODY_PATHS: List<String> = listOf("client", "valid", "register")

/**
 * Builds the SDK's configured [HttpClient].
 *
 * The token lifecycle is **access-token-only with logout-on-401**: there is no refresh endpoint.
 * [accessToken] is read fresh on every request (the bearer cache is disabled, see `cacheTokens`),
 * so a login/logout that changes the persisted token is picked up without any external cache
 * invalidation. On a 401 the bearer provider invokes [onUnauthorized] — the SDK's logout hook, not a
 * token refresh — and then surfaces [uz.yalla.network.error.DataError.Network.Unauthorized] to the
 * caller via [safeApiCall].
 *
 * @param accessToken supplies the current bearer token (or `null`/blank for guest mode); polled per request.
 * @param locale emits the `lang` header value; omitted from a request while blank.
 * @param guestMode emits whether the user is unauthenticated; gates the guest-path allowlist.
 * @param position emits the device location for the `x-position` header; omitted while unset ([GeoPoint.Zero]).
 * @param onUnauthorized invoked on a 401 to drive logout/re-auth. May be called concurrently across
 *   clients, so the implementation must be idempotent.
 * @param scope retained for the caller's lifetime contract; the SDK's own warm-up collectors are bound
 *   to the returned client and torn down by [HttpClient.close], not by this scope.
 */
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
                    // Never let credentials or precise location reach a log sink, even in non-prod.
                    // The bearer token, the static secret-key and the GPS header would otherwise be
                    // printed verbatim on every QA/staging device (CWE-532). Redaction is unconditional
                    // so an integrator cannot leak them by flipping the logging flag.
                    SENSITIVE_HEADERS.forEach { name -> sanitizeHeader { it.equals(name, ignoreCase = true) } }
                    // sanitizeHeader covers headers but not bodies. The onboarding/auth endpoints carry
                    // OTPs, phone numbers and freshly minted tokens in their request/response bodies, so
                    // skip logging those calls entirely. Every other call is still logged with redacted
                    // headers, keeping the log useful.
                    filter { request ->
                        SENSITIVE_BODY_PATHS.none { isGuestAllowedPath(request.url.encodedPath, setOf(it)) }
                    }
                }
            }

            install(Auth) {
                bearer {
                    // cacheTokens=false: re-read accessToken() on every request so a login/logout that
                    // changes the persisted token is picked up immediately. With the default (true), the
                    // token is loaded once (typically as a guest, i.e. null) and never refreshed, which
                    // forces consumers to reach into Ktor internals to invalidate the cache.
                    cacheTokens = false
                    loadTokens {
                        val token = accessToken()
                        // isNullOrBlank, not isNullOrEmpty: a whitespace-only token must be treated as
                        // "no token" (guest), never sent as a malformed `Authorization: Bearer ` header.
                        if (token.isNullOrBlank()) null else BearerTokens(accessToken = token, refreshToken = "")
                    }
                    // Not a refresh: the backend has no refresh endpoint. Returning null after firing the
                    // logout hook means "session is dead", and the final 401 maps to Unauthorized.
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
                // secretKey is a build-time constant baked into the binary; see [NetworkConfig.secretKey].
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
                        // Skip the header until a real fix lands: GeoPoint.Zero is the unset sentinel, and
                        // sending "0.0 0.0" as a real coordinate yields wrong tariff/geocode estimates at
                        // cold start. formatPosition pins the wire string identically on JVM and K/N.
                        if (location != GeoPoint.Zero) {
                            request.headers.set("x-position", formatPosition(location))
                        }
                    }
                }
            )
        }

    // Bind the warm-up collectors to the CLIENT's lifecycle, not the caller's scope: collectLatest
    // never completes, so launching on `scope` leaks 3 collectors per client. The client's own
    // coroutineContext is cancelled by close(), so these tear down deterministically.
    client.launch { locale.collectLatest { localeCache.value = it } }
    client.launch { guestMode.collectLatest { guestModeCache.value = it } }
    client.launch { position.collectLatest { positionCache.value = it } }

    return client
}
