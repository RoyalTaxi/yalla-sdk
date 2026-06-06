package uz.yalla.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

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

    val SESSION_KEYS: List<Preferences.Key<*>> = listOf(

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

    val LOCALE_TYPE = stringPreferencesKey("localeType")

    val THEME_TYPE = stringPreferencesKey("themeType")

    val MAP_TYPE = stringPreferencesKey("mapType")

    val SKIP_ONBOARDING = booleanPreferencesKey("skipOnboarding")

    val LAST_MAP_POSITION = stringPreferencesKey("lastMapPosition")

    val LAST_GPS_POSITION = stringPreferencesKey("lastGpsPosition")
}
