package uz.yalla.core.settings

import uz.yalla.core.util.normalizedId

public enum class ThemeKind(
    public val id: String
) {
    Light("light"),

    Dark("dark"),

    System("system");

    public companion object {
        public fun from(id: String?): ThemeKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: System
        }
    }
}
