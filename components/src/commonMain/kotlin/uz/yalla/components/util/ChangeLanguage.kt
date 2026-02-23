package uz.yalla.components.util

/**
 * Set the app's language.
 *
 * @param language Language code (e.g., "en", "uz", "ru")
 */
expect fun setLanguage(language: String)

/**
 * Get the current app language.
 *
 * @return Current language code
 */
expect fun getLanguage(): String
