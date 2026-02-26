package uz.yalla.core.kind

import kotlinx.serialization.Serializable

/** App theme options. */
@Serializable
enum class ThemeKind(val id: String) {
    Light("light"),
    Dark("dark"),
    System("system");

    companion object {
        fun from(id: String?): ThemeKind = entries.find { it.id == id } ?: System
    }
}
