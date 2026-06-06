package uz.yalla.core.settings

import uz.yalla.core.util.normalizedId

enum class MapKind(
    val id: String
) {
    Google("google"),

    Libre("libre");

    companion object {
        fun from(id: String?): MapKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Google
        }
    }
}
