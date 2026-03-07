package uz.yalla.data.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.StateFlow

/**
 * Exception thrown when a guest user attempts a restricted API call.
 *
 * @see createGuestModeGuardPlugin
 * @since 0.0.1
 */
internal class GuestBlockedException : RuntimeException()

/**
 * Ktor plugin that blocks API calls not in [allowedSegments] when guest mode is active.
 *
 * Checks the last path segment of each request URL against the whitelist.
 * Throws [GuestBlockedException] for unauthorized requests.
 *
 * @param isGuestMode reactive guest mode state
 * @param allowedSegments URL segments permitted in guest mode
 * @since 0.0.1
 */
fun createGuestModeGuardPlugin(
    isGuestMode: StateFlow<Boolean>,
    allowedSegments: Set<String> = DEFAULT_GUEST_ALLOWED_SEGMENTS,
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

private val DEFAULT_GUEST_ALLOWED_SEGMENTS =
    setOf(
        "client",
        "valid",
        "register",
        "location-name",
        "cost",
        "lists",
    )
