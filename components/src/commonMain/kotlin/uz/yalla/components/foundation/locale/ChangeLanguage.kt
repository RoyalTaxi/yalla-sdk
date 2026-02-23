package uz.yalla.components.foundation.locale

/**
 * Platform-specific language change handler.
 *
 * Implement actual functions in androidMain and iosMain.
 */
expect fun changeLanguage(languageCode: String)

/**
 * Gets the current system language code.
 *
 * @return ISO 639-1 language code (e.g., "en", "uz", "ru")
 */
expect fun getCurrentLanguage(): String
