package uz.yalla.core.settings

import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/**
 * App theme options for controlling light/dark appearance.
 *
 * [System] follows the device's system-wide dark mode setting.
 * [Light] and [Dark] override it with a fixed appearance.
 *
 * @property id Wire-format identifier used in persistence
 * @see uz.yalla.core.contract.preferences.InterfacePreferences.themeType
 * @since 0.0.1
 */
@Serializable
enum class ThemeKind(val id: String) {
    /** Always light theme, regardless of system setting. */
    Light("light"),

    /** Always dark theme, regardless of system setting. */
    Dark("dark"),

    /** Follow the device's system-wide appearance setting. Default. */
    System("system");

    companion object {
        /**
         * Parses a stored/API string into the corresponding [ThemeKind].
         *
         * Performs case-insensitive matching after trimming whitespace.
         * Returns [System] for `null` or unrecognized values.
         *
         * @param id Wire-format identifier, or `null`
         * @return The matching [ThemeKind], defaulting to [System]
         */
        fun from(id: String?): ThemeKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: System
        }
    }
}
