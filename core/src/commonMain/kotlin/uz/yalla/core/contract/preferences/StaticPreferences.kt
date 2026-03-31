package uz.yalla.core.contract.preferences

/**
 * Synchronous key-value store for values that must be readable
 * without coroutine context (e.g. during app startup before
 * the first frame is rendered).
 *
 * Backed by platform-native synchronous storage
 * (SharedPreferences on Android, NSUserDefaults on iOS).
 *
 * @since 0.0.7
 */
interface StaticPreferences {
    val localeCode: String
    val isDeviceRegistered: Boolean
    val isGuestMode: Boolean
    val onboardingStage: String

    fun setLocaleCode(value: String)
    fun setDeviceRegistered(value: Boolean)
    fun setGuestMode(value: Boolean)
    fun setOnboardingStage(value: String)
}
