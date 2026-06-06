package uz.yalla.foundation.locale

import java.util.Locale

actual fun changeLanguage(languageCode: String) {
    Locale.setDefault(Locale.forLanguageTag(languageCode))
}

actual fun getCurrentLanguage(): String = Locale.getDefault().language
