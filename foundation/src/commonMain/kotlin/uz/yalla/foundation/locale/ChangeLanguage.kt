package uz.yalla.foundation.locale

/**
 * Platform-specific language change handler.
 *
 * Implement actual functions in androidMain and iosMain.
 *
 * @param languageCode ISO 639-1 language code
 * @since 0.0.1
 */
expect fun changeLanguage(languageCode: String)

/**
 * Gets the current system language code.
 *
 * @return ISO 639-1 language code (e.g., "en", "uz", "ru")
 * @since 0.0.1
 */
expect fun getCurrentLanguage(): String
