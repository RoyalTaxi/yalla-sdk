package uz.yalla.core.contract.preferences

/**
 * Synchronous key-value store for values that must be readable
 * without coroutine context (e.g., during app startup before
 * the first frame is rendered).
 *
 * Backed by platform-native synchronous storage
 * (SharedPreferences on Android, NSUserDefaults on iOS).
 *
 * This is the synchronous counterpart to [SessionPreferences] and
 * [InterfacePreferences], providing the same subset of values without
 * requiring a coroutine scope.
 *
 * ## Usage
 * ```kotlin
 * // Safe to call from any context, including Application.onCreate
 * val locale = staticPreferences.localeCode
 * val isGuest = staticPreferences.isGuestMode
 * ```
 *
 * @see SessionPreferences for the reactive (Flow-based) equivalent
 * @see InterfacePreferences for the reactive locale/onboarding equivalent
 * @since 0.0.7
 */
interface StaticPreferences {

    /**
     * Current app locale code (e.g., "uz", "ru", "en").
     *
     * @see InterfacePreferences.localeType for the reactive equivalent
     */
    val localeCode: String

    /**
     * Whether this device has been registered with the backend.
     *
     * @see SessionPreferences.isDeviceRegistered for the reactive equivalent
     */
    val isDeviceRegistered: Boolean

    /**
     * Whether the user is browsing without authentication.
     *
     * @see SessionPreferences.isGuestMode for the reactive equivalent
     */
    val isGuestMode: Boolean

    /**
     * Current onboarding progress stage identifier.
     *
     * @see InterfacePreferences.onboardingStage for the reactive equivalent
     */
    val onboardingStage: String

    /**
     * Persists the locale code synchronously.
     *
     * @param value Locale code string (e.g., "uz", "ru", "en")
     */
    fun setLocaleCode(value: String)

    /**
     * Persists the device registration flag synchronously.
     *
     * @param value `true` if the device has been registered
     */
    fun setDeviceRegistered(value: Boolean)

    /**
     * Persists the guest mode flag synchronously.
     *
     * @param value `true` if the user is in guest mode
     */
    fun setGuestMode(value: Boolean)

    /**
     * Persists the onboarding stage synchronously.
     *
     * @param value Stage identifier string
     */
    fun setOnboardingStage(value: String)
}
