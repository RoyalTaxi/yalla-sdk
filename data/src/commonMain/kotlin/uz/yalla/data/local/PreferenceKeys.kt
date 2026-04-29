package uz.yalla.data.local

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Central registry of all [DataStore][androidx.datastore.core.DataStore] preference keys.
 *
 * Keeping keys in one place prevents accidental key collisions
 * across the separate preferences implementations that share a
 * single [DataStore][androidx.datastore.core.DataStore] instance.
 *
 * Keys are grouped by domain:
 * - **Session** -- authentication tokens and device registration state
 * - **User** -- profile data (name, phone, payment method)
 * - **Config** -- server-provided configuration (support contacts, bonus limits)
 * - **Interface** -- user-facing settings (locale, theme, map, onboarding)
 * - **Position** -- geographic coordinates stored as `"lat,lng"` strings
 *
 * @see SessionPreferencesImpl
 * @see UserPreferencesImpl
 * @see ConfigPreferencesImpl
 * @see InterfacePreferencesImpl
 * @see PositionPreferencesImpl
 */
internal object PreferenceKeys {

    // region Session

    /** OAuth access token for authenticated API requests. */
    val ACCESS_TOKEN = stringPreferencesKey("accessToken")

    /** Firebase Cloud Messaging registration token. */
    val FIREBASE_TOKEN = stringPreferencesKey("firebaseToken")

    /** Whether the user is browsing in guest (unauthenticated) mode. */
    val IS_GUEST_MODE = booleanPreferencesKey("isGuestModeEnable")

    /** Whether the device has been registered with the backend device registry (distinct from FCM token upload). */
    val IS_DEVICE_REGISTERED = booleanPreferencesKey("isDeviceRegistered")

    // endregion

    // region User

    val FIRST_NAME = stringPreferencesKey("firstName")

    val LAST_NAME = stringPreferencesKey("lastName")

    /** User's phone number in international format. */
    val NUMBER = stringPreferencesKey("number")

    /** Selected payment method identifier (e.g. `"cash"`, `"card"`). */
    val PAYMENT_TYPE = stringPreferencesKey("paymentType")

    /** Stored card identifier (raw `CardId.raw`); blank or absent makes [PaymentKind.from] resolve to `Cash`. */
    val CARD_ID = stringPreferencesKey("cardId")

    /** Masked card number displayed in the UI (e.g. `"**** 1234"`). */
    val CARD_NUMBER = stringPreferencesKey("cardNumber")

    // endregion

    // region Config

    val SUPPORT_NUMBER = stringPreferencesKey("supportNumber")

    /** Customer support Telegram deep-link or username. */
    val SUPPORT_TELEGRAM = stringPreferencesKey("supportTelegram")

    val INFO_INSTAGRAM = stringPreferencesKey("infoInstagram")

    /** Brand Telegram channel URL. */
    val INFO_TELEGRAM = stringPreferencesKey("infoTelegram")

    /** Privacy policy URL for the Russian locale. */
    val PRIVACY_POLICY_RU = stringPreferencesKey("privacyPolicyRu")

    /** Privacy policy URL for the Uzbek locale. */
    val PRIVACY_POLICY_UZ = stringPreferencesKey("privacyPolicyUz")

    /** Maximum bonus amount a user can apply to an order. */
    val MAX_BONUS = longPreferencesKey("maxBonus")

    /** Minimum bonus amount required to apply bonuses to an order. */
    val MIN_BONUS = longPreferencesKey("minBonus")

    val BALANCE = longPreferencesKey("balance")

    /** Whether the bonus payment option is enabled by the backend. */
    val IS_BONUS_ENABLED = booleanPreferencesKey("isBonusEnabled")

    /** Whether the card payment option is enabled by the backend. */
    val IS_CARD_ENABLED = booleanPreferencesKey("isCardEnabled")

    /** Allowed cancellation window in seconds after order creation. */
    val ORDER_CANCEL_TIME = intPreferencesKey("orderCancelTime")

    // endregion

    /**
     * Keys that belong to an authenticated session (auth, user profile, server config).
     * [SessionPreferencesImpl.clearSession] removes exactly these keys on logout,
     * while interface and position keys survive.
     */
    val SESSION_KEYS: List<Preferences.Key<*>> = listOf(
        // Session
        ACCESS_TOKEN,
        FIREBASE_TOKEN,
        IS_GUEST_MODE,
        IS_DEVICE_REGISTERED,
        // User
        FIRST_NAME,
        LAST_NAME,
        NUMBER,
        PAYMENT_TYPE,
        CARD_ID,
        CARD_NUMBER,
        // Config
        SUPPORT_NUMBER,
        SUPPORT_TELEGRAM,
        INFO_INSTAGRAM,
        INFO_TELEGRAM,
        PRIVACY_POLICY_RU,
        PRIVACY_POLICY_UZ,
        MAX_BONUS,
        MIN_BONUS,
        BALANCE,
        IS_BONUS_ENABLED,
        IS_CARD_ENABLED,
        ORDER_CANCEL_TIME,
    )

    // region Interface

    /** Active locale code (e.g. `"uz"`, `"ru"`). */
    val LOCALE_TYPE = stringPreferencesKey("localeType")

    /** Active theme identifier (e.g. `"light"`, `"dark"`, `"system"`). */
    val THEME_TYPE = stringPreferencesKey("themeType")

    /** Active map provider identifier (e.g. `"google"`, `"maplibre"`). */
    val MAP_TYPE = stringPreferencesKey("mapType")

    /** When `true`, the onboarding flow is bypassed entirely regardless of [ONBOARDING_STAGE]. */
    val SKIP_ONBOARDING = booleanPreferencesKey("skipOnboarding")

    /** Current onboarding stage identifier (default [DEFAULT_ONBOARDING_STAGE]). */
    val ONBOARDING_STAGE = stringPreferencesKey("onboardingStage")

    /** Default value for [ONBOARDING_STAGE] when no stage has been persisted yet. */
    const val DEFAULT_ONBOARDING_STAGE = "FRESH"

    // endregion

    // region Position

    /** Last map-center position stored as `"lat,lng"`. */
    val LAST_MAP_POSITION = stringPreferencesKey("lastMapPosition")

    /** Last GPS fix stored as `"lat,lng"`. Falls back to [LAST_MAP_POSITION]. */
    val LAST_GPS_POSITION = stringPreferencesKey("lastGpsPosition")

    // endregion
}
