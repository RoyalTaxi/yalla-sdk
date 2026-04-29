package uz.yalla.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/**
 * Map rendering provider options.
 *
 * Controls which map SDK is used for rendering tiles, markers, and routes.
 * [Google] uses Google Maps SDK (requires API key), while [Libre] uses
 * the open-source MapLibre GL Native library.
 *
 * @see uz.yalla.core.preferences.InterfacePreferences.mapKind
 */
@Serializable
enum class MapKind(val id: String) {
    /** Google Maps SDK. Default on Android. */
    @SerialName("google")
    Google("google"),

    /** MapLibre GL Native. Default on iOS. */
    @SerialName("libre")
    Libre("libre");

    companion object {
        /**
         * Parses a stored/API string into the corresponding [MapKind].
         *
         * Performs case-insensitive matching after trimming whitespace.
         * Returns [Google] for `null` or unrecognized values.
         *
         * @return The matching [MapKind], defaulting to [Google]
         */
        fun from(id: String?): MapKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Google
        }
    }
}
