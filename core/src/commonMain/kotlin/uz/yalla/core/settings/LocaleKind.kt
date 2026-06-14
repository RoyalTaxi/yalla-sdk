package uz.yalla.core.settings


public enum class LocaleKind(
    public val code: String,
    public val displayName: String
) {
    Uz("uz", "O'zbekcha"),

    Ru("ru", "Русский");

    public companion object {
        public fun from(code: String?): LocaleKind {
            val normalized = code
                ?.trim()
                ?.replace('_', '-')
                ?.lowercase()
                .orEmpty()
            return entries.find { it.code.lowercase() == normalized } ?: Uz
        }
    }
}
