package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.Settings

expect fun createDataStore(): DataStore<Preferences>

expect fun createStaticSettings(): Settings
