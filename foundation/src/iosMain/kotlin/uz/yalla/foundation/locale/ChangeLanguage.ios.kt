package uz.yalla.foundation.locale

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import uz.yalla.core.settings.LocaleKind

private const val APPLE_LANGUAGES_KEY = "AppleLanguages"

public actual fun changeLanguage(languageCode: String) {
    val defaults = NSUserDefaults.standardUserDefaults
    val existing =
        (defaults.arrayForKey(APPLE_LANGUAGES_KEY) as? List<*>)
            .orEmpty()
            .filterIsInstance<String>()
            .filter { it != languageCode }
    defaults.setObject(listOf(languageCode) + existing, forKey = APPLE_LANGUAGES_KEY)
}

public actual fun getCurrentLanguage(): String {
    val persisted =
        (NSUserDefaults.standardUserDefaults.arrayForKey(APPLE_LANGUAGES_KEY) as? List<*>)
            ?.filterIsInstance<String>()
            ?.firstOrNull()
            ?.substringBefore('-')
            ?.takeIf { it.isNotBlank() }
    return persisted ?: NSLocale.currentLocale.languageCode ?: LocaleKind.Uz.code
}
