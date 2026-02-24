package uz.yalla.foundation.locale

import java.util.Locale

/**
 * Changes the app language on Android.
 *
 * Note: Full implementation requires Activity recreation or AppCompatDelegate.
 * This is a basic implementation - integrate with your app's locale handling.
 *
 * @param languageCode ISO 639-1 language code
 */
actual fun changeLanguage(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
}

/**
 * Gets current system language on Android.
 *
 * @return ISO 639-1 language code
 */
actual fun getCurrentLanguage(): String = Locale.getDefault().language
