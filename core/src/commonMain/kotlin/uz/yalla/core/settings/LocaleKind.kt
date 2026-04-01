package uz.yalla.core.settings

import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedLocaleCode

/**
 * Supported app locales for string resource selection.
 *
 * Determines which language and script variant is used throughout the app.
 * The [code] follows BCP 47 conventions (e.g., "uz-Cyrl" for Uzbek in Cyrillic script).
 *
 * @property code BCP 47 locale code used for resource lookup
 * @see uz.yalla.core.contract.preferences.InterfacePreferences.localeType
 * @since 0.0.1
 */
@Serializable
enum class LocaleKind(val code: String) {
    /** Uzbek (Latin script). Default locale. */
    Uz("uz"),

    /** Uzbek (Cyrillic script). */
    UzCyrillic("uz-Cyrl"),

    /** Russian. */
    Ru("ru"),

    /** English. */
    En("en");

    companion object {
        /**
         * Parses a locale code string into the corresponding [LocaleKind].
         *
         * Normalizes input by trimming, lowercasing, and replacing underscores
         * with hyphens (e.g., "uz_Cyrl" becomes "uz-cyrl").
         * Returns [Uz] for `null` or unrecognized codes.
         *
         * @param code BCP 47 locale code, or `null`
         * @return The matching [LocaleKind], defaulting to [Uz]
         */
        fun from(code: String?): LocaleKind {
            val normalizedCode = code.normalizedLocaleCode()
            return entries.find { it.code.lowercase() == normalizedCode } ?: Uz
        }
    }
}
