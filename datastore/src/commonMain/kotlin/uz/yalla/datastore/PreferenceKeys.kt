package uz.yalla.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import uz.yalla.datastore.PreferenceKeys.SESSION_KEYS

internal object PreferenceKeys {
    val ACCESS_TOKEN = stringPreferencesKey("accessToken")

    val FIREBASE_TOKEN = stringPreferencesKey("firebaseToken")

    val IS_GUEST_MODE = booleanPreferencesKey("isGuestModeEnable")

    val IS_DEVICE_REGISTERED = booleanPreferencesKey("isDeviceRegistered")

    val FIRST_NAME = stringPreferencesKey("firstName")

    val LAST_NAME = stringPreferencesKey("lastName")

    val NUMBER = stringPreferencesKey("number")

    val IMAGE = stringPreferencesKey("image")

    val GENDER = stringPreferencesKey("gender")

    val BIRTHDAY = stringPreferencesKey("birthday")

    val PAYMENT_TYPE = stringPreferencesKey("paymentType")

    val CARD_ID = stringPreferencesKey("cardId")

    val CARD_NUMBER = stringPreferencesKey("cardNumber")

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

    val SESSION_KEYS: List<Preferences.Key<*>> =
        listOf(
            ACCESS_TOKEN,
            FIREBASE_TOKEN,
            IS_GUEST_MODE,
            IS_DEVICE_REGISTERED,
            FIRST_NAME,
            LAST_NAME,
            NUMBER,
            IMAGE,
            GENDER,
            BIRTHDAY,
            PAYMENT_TYPE,
            CARD_ID,
            CARD_NUMBER,
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
            ORDER_CANCEL_TIME
        )

    /**
     * The SENSITIVE keys routed through [SecureStore] (encrypted at rest) instead of the plain DataStore:
     * the auth + push tokens and the profile/payment PII (name, phone, birthday, gender, avatar URL, card
     * id + masked PAN). Identified by their logical name so the secure store, the migration, and the clears
     * stay 1:1 with the matching plain key. NON-sensitive keys (guest/device flags, the `PAYMENT_TYPE`
     * cash/card discriminator, config, and all UX prefs) deliberately stay in plain DataStore.
     *
     * Every entry here is also in [SESSION_KEYS], so a session reset scrubs both the encrypted value and any
     * un-migrated legacy plaintext.
     */
    val SECURE_KEYS: List<String> =
        listOf(
            ACCESS_TOKEN,
            FIREBASE_TOKEN,
            FIRST_NAME,
            LAST_NAME,
            NUMBER,
            IMAGE,
            GENDER,
            BIRTHDAY,
            CARD_ID,
            CARD_NUMBER
        ).map { it.name }

    val LOCALE_TYPE = stringPreferencesKey("localeType")

    val THEME_TYPE = stringPreferencesKey("themeType")

    val MAP_TYPE = stringPreferencesKey("mapType")

    val SKIP_ONBOARDING = booleanPreferencesKey("skipOnboarding")

    val LAST_MAP_POSITION = stringPreferencesKey("lastMapPosition")

    val LAST_GPS_POSITION = stringPreferencesKey("lastGpsPosition")
}
