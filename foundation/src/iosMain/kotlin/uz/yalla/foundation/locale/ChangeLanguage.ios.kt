package uz.yalla.foundation.locale

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

/**
 * Sets the AppleLanguages user default. App restart may be required.
 */
actual fun changeLanguage(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(languageCode),
        forKey = "AppleLanguages"
    )
}

actual fun getCurrentLanguage(): String = NSLocale.currentLocale.languageCode ?: "uz"
