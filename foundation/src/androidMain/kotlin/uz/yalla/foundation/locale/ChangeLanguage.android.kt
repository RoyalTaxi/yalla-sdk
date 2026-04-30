package uz.yalla.foundation.locale

import java.util.Locale

/**
 * **Limitation:** Only calls [Locale.setDefault]. For full in-app language change,
 * the Activity must be recreated or AppCompatDelegate.setApplicationLocales used.
 * This function alone does NOT update visible UI.
 */
actual fun changeLanguage(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
}

actual fun getCurrentLanguage(): String = Locale.getDefault().language
