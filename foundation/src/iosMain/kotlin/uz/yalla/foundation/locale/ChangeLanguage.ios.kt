package uz.yalla.foundation.locale

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

public actual fun changeLanguage(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(languageCode),
        forKey = "AppleLanguages"
    )
}

public actual fun getCurrentLanguage(): String = NSLocale.currentLocale.languageCode ?: "uz"
