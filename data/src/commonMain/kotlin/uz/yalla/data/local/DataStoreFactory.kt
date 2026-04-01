package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Creates the platform-specific [DataStore] instance for preferences storage.
 *
 * Android stores data in the app's internal files directory.
 * iOS stores data in the documents directory.
 *
 * The returned instance is registered as a Koin singleton and shared
 * by all `*PreferencesImpl` classes.
 *
 * @return platform-specific [DataStore] for reading and writing preferences
 * @see uz.yalla.data.di.dataModule
 * @see SessionPreferencesImpl
 * @see UserPreferencesImpl
 * @see ConfigPreferencesImpl
 * @see InterfacePreferencesImpl
 * @see PositionPreferencesImpl
 * @since 0.0.1
 */
expect fun createDataStore(): DataStore<Preferences>
