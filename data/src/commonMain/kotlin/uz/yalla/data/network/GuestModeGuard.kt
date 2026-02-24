package uz.yalla.data.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.encodedPath
import uz.yalla.core.contract.StaticPreferences
import uz.yalla.core.error.GuestModeRequestBlocked

private val guestAllowedSegments =
    setOf(
        "client",
        "valid",
        "register",
        "location-name",
        "cost",
        "lists"
    )

fun createGuestModeGuardPlugin(staticPrefs: StaticPreferences) =
    createClientPlugin("GuestModeGuard") {
        onRequest { request, _ ->
            if (!staticPrefs.isGuestModeEnable) return@onRequest

            val path = request.url.encodedPath.trimEnd('/')
            val lastSegment = path.substringAfterLast('/')
            if (lastSegment !in guestAllowedSegments) {
                throw GuestModeRequestBlocked()
            }
        }
    }
