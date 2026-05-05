package uz.yalla.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/**
 * App theme options for controlling light/dark appearance.
 *
 * [System] follows the device's system-wide dark mode setting.
 * [Light] and [Dark] override it with a fixed appearance.
 *
 * @see uz.yalla.core.preferences.InterfacePreferences.themeType
 */
@Serializable
enum class ThemeKind(
    val id: String
) {
    /** Always light theme, regardless of system setting. */
    @SerialName("light")
    Light("light"),

    /** Always dark theme, regardless of system setting. */
    @SerialName("dark")
    Dark("dark"),

    /** Follow the device's system-wide appearance setting. Default. */
    @SerialName("system")
    System("system");

    companion object {
        /**
         * Parses a stored/API string into the corresponding [ThemeKind].
         *
         * Performs case-insensitive matching after trimming whitespace.
         * Returns [System] for `null` or unrecognized values.
         *
         * @return The matching [ThemeKind], defaulting to [System]
         */
        fun from(id: String?): ThemeKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: System
        }
    }
}
