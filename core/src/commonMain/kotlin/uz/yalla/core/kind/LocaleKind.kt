package uz.yalla.core.kind

import kotlinx.serialization.Serializable

/** Supported app locales with display names. */
@Serializable
enum class LocaleKind(
    val code: String,
    val displayName: String
) {
    Uz("uz", "O'zbekcha"),
    UzCyrillic("uz-Cyrl", "Ўзбекча"),
    Ru("ru", "Русский"),
    En("en", "English");

    companion object {
        fun from(code: String?): LocaleKind = entries.find { it.code == code } ?: Uz
    }
}
