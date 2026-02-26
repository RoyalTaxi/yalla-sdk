package uz.yalla.core.kind

import kotlinx.serialization.Serializable

/** Map provider options. */
@Serializable
enum class MapKind(val id: String) {
    Google("google"),
    Libre("libre");

    companion object {
        fun from(id: String?): MapKind = entries.find { it.id == id } ?: Google
    }
}
