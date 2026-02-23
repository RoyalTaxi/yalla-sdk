package uz.yalla.core.contract

import uz.yalla.core.kind.LocaleKind

interface StaticPreferences {
    var skipOnboarding: Boolean
    var isGuestModeEnable: Boolean
    var isDeviceRegistered: Boolean
    var hasInjectedOrderOnEntry: Boolean
    var localeType: LocaleKind

    fun performLogout()
}
