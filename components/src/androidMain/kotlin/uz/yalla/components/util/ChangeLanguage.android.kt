package uz.yalla.components.util

import java.util.Locale

actual fun setLanguage(language: String) {
    Locale.setDefault(Locale.forLanguageTag(language))
}

actual fun getLanguage(): String = Locale.getDefault().language
