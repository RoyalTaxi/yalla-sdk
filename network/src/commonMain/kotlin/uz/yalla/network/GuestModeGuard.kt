package uz.yalla.network

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.StateFlow

internal class GuestBlockedException : RuntimeException()

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

internal fun createGuestModeGuardPlugin(
    isGuestMode: StateFlow<Boolean>,
    allowedPaths: Set<String> = DEFAULT_GUEST_ALLOWED_PATHS.toSet()
): ClientPlugin<Unit> {
    val normalized = allowedPaths.mapNotNull { it.trim('/').takeIf(String::isNotEmpty) }.toSet()
    return createClientPlugin("GuestModeGuard") {
        onRequest { request, _ ->
            if (!isGuestMode.value) return@onRequest
            if (!isGuestAllowedPath(request.url.encodedPath, normalized)) {
                throw GuestBlockedException()
            }
        }
    }
}
