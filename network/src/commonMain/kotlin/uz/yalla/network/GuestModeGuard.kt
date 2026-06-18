package uz.yalla.network

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.StateFlow

internal class GuestBlockedException : RuntimeException()

/**
 * Decides whether [encodedPath] may be requested while the SDK is in guest mode.
 *
 * An entry in [allowedPaths] is a full relative endpoint path (e.g. `address/tariff/cost`).
 * A request is allowed when its path equals an entry or ends with `"/$entry"` — i.e. the entry
 * is a whole-segment suffix of the path. Matching the suffix (rather than the bare last segment)
 * keeps multi-segment endpoints intact (`admin/cost` no longer slips through `cost`) while staying
 * robust to whatever base path the backend prepends, which differs across the PHP/GO backends.
 */
internal fun isGuestAllowedPath(
    encodedPath: String,
    allowedPaths: Set<String>
): Boolean {
    val path = encodedPath.trim('/')
    return allowedPaths.any { entry ->
        val target = entry.trim('/')
        target.isNotEmpty() && (path == target || path.endsWith("/$target"))
    }
}

public fun createGuestModeGuardPlugin(
    isGuestMode: StateFlow<Boolean>,
    allowedPaths: Set<String> = DEFAULT_GUEST_ALLOWED_PATHS.toSet()
): ClientPlugin<Unit> =
    createClientPlugin("GuestModeGuard") {
        onRequest { request, _ ->
            if (!isGuestMode.value) return@onRequest
            if (!isGuestAllowedPath(request.url.encodedPath, allowedPaths)) {
                throw GuestBlockedException()
            }
        }
    }
