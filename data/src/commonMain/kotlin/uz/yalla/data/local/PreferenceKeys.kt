package uz.yalla.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Central registry of all [DataStore] preference keys.
 *
 * Keeping keys in one place prevents accidental key collisions
 * across the separate preferences implementations that share a
 * single [DataStore] instance.
 */
internal object PreferenceKeys {
    // Session
    val ACCESS_TOKEN = stringPreferencesKey("accessToken")
    val FIREBASE_TOKEN = stringPreferencesKey("firebaseToken")
    val IS_GUEST_MODE = booleanPreferencesKey("isGuestModeEnable")
    val IS_DEVICE_REGISTERED = booleanPreferencesKey("isDeviceRegistered")

    // User
    val FIRST_NAME = stringPreferencesKey("firstName")
    val LAST_NAME = stringPreferencesKey("lastName")
    val NUMBER = stringPreferencesKey("number")
    val PAYMENT_TYPE = stringPreferencesKey("paymentType")
    val CARD_ID = stringPreferencesKey("cardId")
    val CARD_NUMBER = stringPreferencesKey("cardNumber")

    // Config
    val SUPPORT_NUMBER = stringPreferencesKey("supportNumber")
    val SUPPORT_TELEGRAM = stringPreferencesKey("supportTelegram")
    val INFO_INSTAGRAM = stringPreferencesKey("infoInstagram")
    val INFO_TELEGRAM = stringPreferencesKey("infoTelegram")
    val PRIVACY_POLICY_RU = stringPreferencesKey("privacyPolicyRu")
    val PRIVACY_POLICY_UZ = stringPreferencesKey("privacyPolicyUz")
    val MAX_BONUS = longPreferencesKey("maxBonus")
    val MIN_BONUS = longPreferencesKey("minBonus")
    val BALANCE = longPreferencesKey("balance")
    val IS_BONUS_ENABLED = booleanPreferencesKey("isBonusEnabled")
    val IS_CARD_ENABLED = booleanPreferencesKey("isCardEnabled")
    val ORDER_CANCEL_TIME = intPreferencesKey("orderCancelTime")

    // Interface
    val LOCALE_TYPE = stringPreferencesKey("localeType")
    val THEME_TYPE = stringPreferencesKey("themeType")
    val MAP_TYPE = stringPreferencesKey("mapType")
    val SKIP_ONBOARDING = booleanPreferencesKey("skipOnboarding")
    val ONBOARDING_STAGE = stringPreferencesKey("onboardingStage")

    // Position
    val LAST_MAP_POSITION = stringPreferencesKey("lastMapPosition")
    val LAST_GPS_POSITION = stringPreferencesKey("lastGpsPosition")
}
