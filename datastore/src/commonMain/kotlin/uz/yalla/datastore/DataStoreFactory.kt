package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.scope.Scope

internal const val DATASTORE_FILE: String = "prefs.preferences_pb"

internal expect fun createDataStore(scope: Scope): DataStore<Preferences>
