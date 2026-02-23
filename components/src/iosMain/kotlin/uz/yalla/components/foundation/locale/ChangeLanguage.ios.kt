package uz.yalla.components.foundation.locale

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
 */
actual fun changeLanguage(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(languageCode),
        forKey = "AppleLanguages"
    )
    NSUserDefaults.standardUserDefaults.synchronize()
}

/**
 * Gets current system language on iOS.
 *
 * @return ISO 639-1 language code
 */
actual fun getCurrentLanguage(): String = NSLocale.currentLocale.languageCode ?: "en"
