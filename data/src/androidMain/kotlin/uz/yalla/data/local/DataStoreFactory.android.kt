package uz.yalla.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

private const val DATASTORE_FILE = "prefs.preferences_pb"

actual fun createDataStore(): DataStore<Preferences> =
    object : KoinComponent {
        val context: Context by inject()
    }.run {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                File(context.filesDir, DATASTORE_FILE).absolutePath.toPath()
            }
        )
    }
