package uz.yalla.core.settings


enum class LocaleKind(
    val code: String,
    val displayName: String
) {
    Uz("uz", "O'zbekcha"),

    Ru("ru", "Русский");

    companion object {
        fun from(code: String?): LocaleKind {
            val normalized = code
                ?.trim()
                ?.replace('_', '-')
                ?.lowercase()
                .orEmpty()
            return entries.find { it.code.lowercase() == normalized } ?: Uz
        }
    }
}
