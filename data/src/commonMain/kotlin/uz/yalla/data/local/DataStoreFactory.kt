package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Creates the platform-specific [DataStore] instance for preferences storage.
 *
 * Android stores data in the app's internal files directory.
 * iOS stores data in the documents directory.
 *
 * @since 0.0.1
 */
expect fun createDataStore(): DataStore<Preferences>
