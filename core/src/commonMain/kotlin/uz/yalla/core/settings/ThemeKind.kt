package uz.yalla.core.settings

import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/** App theme options. */
@Serializable
enum class ThemeKind(val id: String) {
    Light("light"),
    Dark("dark"),
    System("system");

    companion object {
        fun from(id: String?): ThemeKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: System
        }
    }
}
