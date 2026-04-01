package uz.yalla.data.local

import com.russhwolf.settings.Settings

/**
 * Creates the platform-specific [Settings] instance for synchronous storage.
 *
 * Android uses SharedPreferences, iOS uses NSUserDefaults.
 * The returned instance is registered as a Koin singleton and consumed
 * by [StaticPreferencesImpl] for startup-critical synchronous reads.
 *
 * @return platform-specific [Settings] for synchronous key-value access
 * @see StaticPreferencesImpl
 * @see uz.yalla.data.di.dataModule
 * @since 0.0.7
 */
expect fun createSettings(): Settings
