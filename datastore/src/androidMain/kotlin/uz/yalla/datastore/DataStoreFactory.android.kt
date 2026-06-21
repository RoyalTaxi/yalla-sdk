package uz.yalla.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import java.io.File

internal actual fun createDataStore(scope: Scope): DataStore<Preferences> {
    val context = scope.get<Context>()
    return PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = {
            File(context.filesDir, DATASTORE_FILE).absolutePath.toPath()
        }
    )
}
