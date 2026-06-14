package uz.yalla.network

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.StateFlow

internal class GuestBlockedException : RuntimeException()

public fun createGuestModeGuardPlugin(
    isGuestMode: StateFlow<Boolean>,
    allowedSegments: Set<String> = DEFAULT_GUEST_ALLOWED_SEGMENTS.toSet()
): ClientPlugin<Unit> =
    createClientPlugin("GuestModeGuard") {
        onRequest { request, _ ->
            if (!isGuestMode.value) return@onRequest

            val path = request.url.encodedPath.trimEnd('/')
            val lastSegment = path.substringAfterLast('/')
            if (lastSegment !in allowedSegments) {
                throw GuestBlockedException()
            }
        }
    }
