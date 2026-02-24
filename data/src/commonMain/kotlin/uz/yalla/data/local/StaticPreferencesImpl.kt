package uz.yalla.data.local

import com.russhwolf.settings.Settings
import uz.yalla.core.contract.StaticPreferences
import uz.yalla.core.kind.LocaleKind

class StaticPreferencesImpl(
    private val settings: Settings
) : StaticPreferences {
    override var skipOnboarding: Boolean
        get() = settings.getBoolean(::skipOnboarding.name, false)
        set(value) = settings.putBoolean(::skipOnboarding.name, value)

    override var isGuestModeEnable: Boolean
        get() = settings.getBoolean(::isGuestModeEnable.name, false)
        set(value) = settings.putBoolean(::isGuestModeEnable.name, value)

    override var isDeviceRegistered: Boolean
        get() = settings.getBoolean(::isDeviceRegistered.name, false)
        set(value) = settings.putBoolean(::isDeviceRegistered.name, value)

    override var hasInjectedOrderOnEntry: Boolean
        get() = settings.getBoolean(::hasInjectedOrderOnEntry.name, false)
        set(value) = settings.putBoolean(::hasInjectedOrderOnEntry.name, value)

    override var localeType: LocaleKind
        get() = LocaleKind.from(settings.getStringOrNull(::localeType.name))
        set(value) = settings.putString(::localeType.name, value.code)

    override fun performLogout() {
        settings.clear()
    }
}
