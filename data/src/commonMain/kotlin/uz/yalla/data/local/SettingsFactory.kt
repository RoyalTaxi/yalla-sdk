package uz.yalla.data.local

import com.russhwolf.settings.Settings

/**
 * Creates the platform-specific [Settings] instance for synchronous storage.
 *
 * Android uses SharedPreferences, iOS uses NSUserDefaults.
 *
 * @since 0.0.7
 */
expect fun createSettings(): Settings
