package uz.yalla.core.settings

import uz.yalla.core.util.normalizedId

public enum class MapKind(
    public val id: String
) {
    Google("google"),

    Libre("libre");

    public companion object {
        public fun from(id: String?): MapKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Google
        }
    }
}
