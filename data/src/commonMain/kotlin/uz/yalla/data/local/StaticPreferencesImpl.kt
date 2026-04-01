package uz.yalla.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import uz.yalla.core.contract.preferences.StaticPreferences
import uz.yalla.core.settings.LocaleKind

/**
 * [Settings]-backed implementation of [StaticPreferences].
 *
 * Provides instant synchronous reads from platform-native storage
 * (SharedPreferences on Android / NSUserDefaults on iOS). Values are
 * kept in sync via dual-write from [SessionPreferencesImpl] and
 * [InterfacePreferencesImpl].
 *
 * This class exists to solve the startup timing problem: [DataStore]
 * reads are asynchronous, but certain values (locale, guest mode,
 * device registration, onboarding stage) must be available immediately
 * at app launch before any coroutine scope is ready.
 *
 * @param settings platform-specific synchronous key-value store
 * @see SessionPreferencesImpl
 * @see InterfacePreferencesImpl
 * @see createSettings
 * @since 0.0.7
 */
internal class StaticPreferencesImpl(
    private val settings: Settings,
) : StaticPreferences {

    override val localeCode: String
        get() = settings[KEY_LOCALE, LocaleKind.Uz.code]

    override val isDeviceRegistered: Boolean
        get() = settings[KEY_DEVICE_REGISTERED, false]

    override val isGuestMode: Boolean
        get() = settings[KEY_GUEST_MODE, false]

    override val onboardingStage: String
        get() = settings[KEY_ONBOARDING_STAGE, FRESH]

    override fun setLocaleCode(value: String) {
        settings[KEY_LOCALE] = value
    }

    override fun setDeviceRegistered(value: Boolean) {
        settings[KEY_DEVICE_REGISTERED] = value
    }

    override fun setGuestMode(value: Boolean) {
        settings[KEY_GUEST_MODE] = value
    }

    override fun setOnboardingStage(value: String) {
        settings[KEY_ONBOARDING_STAGE] = value
    }

    private companion object {
        const val PREFIX = "startup_"
        const val KEY_LOCALE = "${PREFIX}locale"
        const val KEY_DEVICE_REGISTERED = "${PREFIX}device_registered"
        const val KEY_GUEST_MODE = "${PREFIX}guest_mode"
        const val KEY_ONBOARDING_STAGE = "${PREFIX}onboarding_stage"
        const val FRESH = "FRESH"
    }
}
