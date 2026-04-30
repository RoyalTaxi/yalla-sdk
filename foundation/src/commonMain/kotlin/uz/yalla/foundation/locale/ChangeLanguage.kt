package uz.yalla.foundation.locale

/**
 * Platform-specific language change handler.
 *
 * Implement actual functions in androidMain and iosMain.
 */
expect fun changeLanguage(languageCode: String)

expect fun getCurrentLanguage(): String
