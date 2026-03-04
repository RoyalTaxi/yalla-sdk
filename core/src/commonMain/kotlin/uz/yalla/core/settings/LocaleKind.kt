package uz.yalla.core.settings

import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedLocaleCode

/** Supported app locales. */
@Serializable
enum class LocaleKind(val code: String) {
    Uz("uz"),
    UzCyrillic("uz-Cyrl"),
    Ru("ru"),
    En("en");

    companion object {
        fun from(code: String?): LocaleKind {
            val normalizedCode = code.normalizedLocaleCode()
            return entries.find { it.code.lowercase() == normalizedCode } ?: Uz
        }
    }
}
