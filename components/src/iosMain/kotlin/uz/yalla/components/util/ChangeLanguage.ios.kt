package uz.yalla.components.util

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

private const val APPLE_LANGUAGES_KEY = "AppleLanguages"
private const val DEFAULT_LANGUAGE = "uz"

actual fun setLanguage(language: String) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(language), APPLE_LANGUAGES_KEY)
    NSUserDefaults.standardUserDefaults.synchronize()
}

actual fun getLanguage(): String {
    val languages = NSUserDefaults.standardUserDefaults.objectForKey(APPLE_LANGUAGES_KEY) as? List<*>
    val first = languages?.firstOrNull() as? String
    return first?.substringBefore("-") ?: NSLocale.currentLocale.languageCode ?: DEFAULT_LANGUAGE
}
