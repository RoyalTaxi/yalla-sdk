package uz.yalla.datastore

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import uz.yalla.core.preferences.StaticPreferences
import uz.yalla.core.settings.LocaleKind

internal class StaticPreferencesImpl(
    private val settings: Settings
) : StaticPreferences {
    override val localeCode: String
        get() = settings[KEY_LOCALE, LocaleKind.Uz.code]

    override val isDeviceRegistered: Boolean
        get() = settings[KEY_DEVICE_REGISTERED, false]

    override val isGuestMode: Boolean
        get() = settings[KEY_GUEST_MODE, false]

    override fun setLocaleCode(value: String) {
        settings[KEY_LOCALE] = value
    }

    override fun setDeviceRegistered(value: Boolean) {
        settings[KEY_DEVICE_REGISTERED] = value
    }

    override fun setGuestMode(value: Boolean) {
        settings[KEY_GUEST_MODE] = value
    }

    private companion object {
        const val PREFIX = "startup_"
        const val KEY_LOCALE = "${PREFIX}locale"
        const val KEY_DEVICE_REGISTERED = "${PREFIX}device_registered"
        const val KEY_GUEST_MODE = "${PREFIX}guest_mode"
    }
}
