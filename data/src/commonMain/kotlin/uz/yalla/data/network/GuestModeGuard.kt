package uz.yalla.data.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.MutableStateFlow

internal class GuestBlockedException : RuntimeException()

private val guestAllowedSegments =
    setOf(
        "client",
        "valid",
        "register",
        "location-name",
        "cost",
        "lists"
    )

fun createGuestModeGuardPlugin(isGuestMode: MutableStateFlow<Boolean>) =
    createClientPlugin("GuestModeGuard") {
        onRequest { request, _ ->
            if (!isGuestMode.value) return@onRequest

            val path = request.url.encodedPath.trimEnd('/')
            val lastSegment = path.substringAfterLast('/')
            if (lastSegment !in guestAllowedSegments) {
                throw GuestBlockedException()
            }
        }
    }
