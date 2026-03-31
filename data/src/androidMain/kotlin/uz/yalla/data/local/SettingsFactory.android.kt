package uz.yalla.data.local

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val SETTINGS_NAME = "yalla_startup"

actual fun createSettings(): Settings =
    object : KoinComponent {
        val context: Context by inject()
    }.run {
        SharedPreferencesSettings(
            context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)
        )
    }
