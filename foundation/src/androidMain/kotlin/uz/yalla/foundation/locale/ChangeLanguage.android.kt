package uz.yalla.foundation.locale

import uz.yalla.core.settings.LocaleKind
import java.util.Locale

public actual fun changeLanguage(languageCode: String) {
    Locale.setDefault(Locale.forLanguageTag(languageCode))
}

public actual fun getCurrentLanguage(): String =
    Locale.getDefault().language.takeIf { it.isNotBlank() } ?: LocaleKind.Uz.code
