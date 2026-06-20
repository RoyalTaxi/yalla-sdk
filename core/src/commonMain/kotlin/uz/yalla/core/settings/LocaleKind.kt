package uz.yalla.core.settings

/**
 * The app's UI language. Identified by a BCP-47 primary language subtag ([code], e.g. `"uz"`/`"ru"`)
 * rather than an opaque wire id, so it interoperates with platform locale strings.
 */
public enum class LocaleKind(
    public val code: String,
    public val displayName: String
) {
    Uz("uz", "O'zbekcha"),

    Ru("ru", "Русский");

    public companion object {
        /**
         * Parses any common locale spelling into a [LocaleKind], defaulting to [Uz] on unknown/null.
         *
         * Region/script subtags are stripped before matching, so `"ru"`, `"ru-RU"`, `"ru_RU"`, and
         * `"uz-Latn-UZ"` all resolve by their primary language subtag — a `Locale.toLanguageTag()` /
         * `NSLocale.identifier` / `Accept-Language` value maps to the right language, not a silent Uz.
         */
        public fun from(code: String?): LocaleKind {
            val primarySubtag =
                code
                    ?.trim()
                    ?.replace('_', '-')
                    ?.lowercase()
                    ?.substringBefore('-')
                    .orEmpty()
            return entries.find { it.code == primarySubtag } ?: Uz
        }
    }
}
