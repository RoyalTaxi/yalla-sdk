package uz.yalla.core.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedLocaleCode

/**
 * Supported app locales for string resource selection.
 *
 * Determines which language and script variant is used throughout the app.
 * The [code] follows BCP 47 conventions (e.g. `"uz"`, `"ru"`).
 *
 * Narrowed in Phase 3 (ADR-014) to production-ready locales only; `En` and
 * `UzCyrillic` cases were removed. Unknown codes fall back to [Uz] via [from].
 *
 * @property code BCP 47 locale code used for resource lookup.
 * @see uz.yalla.core.preferences.InterfacePreferences.localeType
 * @since 0.0.1
 */
@Serializable
enum class LocaleKind(val code: String) {
    /** Uzbek (Latin script). Default locale. */
    @SerialName("uz")
    Uz("uz"),

    /** Russian. */
    @SerialName("ru")
    Ru("ru");

    companion object {
        /**
         * Parses a locale code into the corresponding [LocaleKind].
         *
         * Normalizes input by trimming, lowercasing, and replacing underscores
         * with hyphens. Returns [Uz] for `null`, unrecognized, or removed codes
         * (e.g. stored `"en"` or `"uz-Cyrl"` from before ADR-014 fall through here).
         *
         * @param code BCP 47 locale code, or `null`.
         * @return Matching [LocaleKind], defaulting to [Uz].
         */
        fun from(code: String?): LocaleKind {
            val normalizedCode = code.normalizedLocaleCode()
            return entries.find { it.code.lowercase() == normalizedCode } ?: Uz
        }
    }
}
