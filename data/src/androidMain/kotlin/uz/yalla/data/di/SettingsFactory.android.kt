package uz.yalla.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

private const val DATASTORE_FILE = "prefs.preferences_pb"
private const val STATIC_PREFS_NAME = "static_preferences"

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

actual fun createStaticSettings(): Settings =
    object : KoinComponent {
        val context: Context by inject()
    }.run {
        SharedPreferencesSettings(
            context.getSharedPreferences(STATIC_PREFS_NAME, Context.MODE_PRIVATE)
        )
    }
