package uz.yalla.foundation.locale

import java.util.Locale

/**
 * Changes the app language on Android.
 *
 * **Limitation:** Only calls [Locale.setDefault]. For full in-app language change,
 * the Activity must be recreated or AppCompatDelegate.setApplicationLocales used.
 * This function alone does NOT update visible UI.
 *
 * @param languageCode ISO 639-1 language code
 * @since 0.0.1
 */
actual fun changeLanguage(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
}

/**
 * Gets current system language on Android.
 *
 * @return ISO 639-1 language code
 * @since 0.0.1
 */
actual fun getCurrentLanguage(): String = Locale.getDefault().language
