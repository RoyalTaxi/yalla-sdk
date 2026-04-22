package uz.yalla.foundation.locale

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

/**
 * Changes the app language on iOS.
 *
 * Sets the AppleLanguages user default. App restart may be required.
 *
 * @param languageCode ISO 639-1 language code
 * @since 0.0.1
 */
actual fun changeLanguage(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(languageCode),
        forKey = "AppleLanguages"
    )
}

/**
 * Gets current system language on iOS.
 *
 * @return ISO 639-1 language code
 * @since 0.0.1
 */
actual fun getCurrentLanguage(): String = NSLocale.currentLocale.languageCode ?: "uz"
