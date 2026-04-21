package uz.yalla.data.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.StateFlow

/**
 * Exception thrown when a guest user attempts a restricted API call.
 *
 * Caught by [safeApiCall] and mapped to [DataError.Network.Guest][uz.yalla.core.error.DataError.Network.Guest].
 *
 * @see createGuestModeGuardPlugin
 * @see safeApiCall
 * @since 0.0.1
 */
internal class GuestBlockedException : RuntimeException()

/**
 * Ktor plugin that blocks API calls not in [allowedSegments] when guest mode is active.
 *
 * Checks the last path segment of each request URL against the whitelist.
 * Throws [GuestBlockedException] for unauthorized requests, which is then
 * caught and mapped to [DataError.Network.Guest][uz.yalla.core.error.DataError.Network.Guest]
 * by [safeApiCall].
 *
 * The default [allowedSegments] mirrors
 * [DEFAULT_GUEST_ALLOWED_SEGMENTS][uz.yalla.data.network.DEFAULT_GUEST_ALLOWED_SEGMENTS]
 * so direct callers keep the legacy whitelist. [createHttpClient] sources the
 * set from [NetworkConfig.guestAllowedSegments] instead, letting integrators
 * override the whitelist per environment without touching this plugin.
 *
 * @param isGuestMode reactive guest mode state
 * @param allowedSegments URL segments permitted in guest mode, defaults to the
 *   legacy six-endpoint whitelist
 * @return Ktor client plugin instance
 * @throws GuestBlockedException when a request is blocked in guest mode
 * @see safeApiCall
 * @see NetworkConfig.guestAllowedSegments
 * @since 0.0.1
 */
fun createGuestModeGuardPlugin(
    isGuestMode: StateFlow<Boolean>,
    allowedSegments: Set<String> = DEFAULT_GUEST_ALLOWED_SEGMENTS.toSet(),
) = createClientPlugin("GuestModeGuard") {
    onRequest { request, _ ->
        if (!isGuestMode.value) return@onRequest

        val path = request.url.encodedPath.trimEnd('/')
        val lastSegment = path.substringAfterLast('/')
        if (lastSegment !in allowedSegments) {
            throw GuestBlockedException()
        }
    }
}
