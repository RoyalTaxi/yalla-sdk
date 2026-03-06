package uz.yalla.data.local

import kotlin.test.Test
import kotlin.test.assertEquals

class PreferenceKeysTest {
    @Test
    fun shouldHaveUniqueKeyNames() {
        val keys = listOf(
            PreferenceKeys.ACCESS_TOKEN,
            PreferenceKeys.FIREBASE_TOKEN,
            PreferenceKeys.IS_GUEST_MODE,
            PreferenceKeys.IS_DEVICE_REGISTERED,
            PreferenceKeys.FIRST_NAME,
            PreferenceKeys.LAST_NAME,
            PreferenceKeys.NUMBER,
            PreferenceKeys.PAYMENT_TYPE,
            PreferenceKeys.CARD_ID,
            PreferenceKeys.CARD_NUMBER,
            PreferenceKeys.SUPPORT_NUMBER,
            PreferenceKeys.SUPPORT_TELEGRAM,
            PreferenceKeys.INFO_INSTAGRAM,
            PreferenceKeys.INFO_TELEGRAM,
            PreferenceKeys.PRIVACY_POLICY_RU,
            PreferenceKeys.PRIVACY_POLICY_UZ,
            PreferenceKeys.MAX_BONUS,
            PreferenceKeys.MIN_BONUS,
            PreferenceKeys.BALANCE,
            PreferenceKeys.IS_BONUS_ENABLED,
            PreferenceKeys.IS_CARD_ENABLED,
            PreferenceKeys.ORDER_CANCEL_TIME,
            PreferenceKeys.LOCALE_TYPE,
            PreferenceKeys.THEME_TYPE,
            PreferenceKeys.MAP_TYPE,
            PreferenceKeys.SKIP_ONBOARDING,
            PreferenceKeys.LAST_MAP_POSITION,
            PreferenceKeys.LAST_GPS_POSITION,
        )
        val keyNames = keys.map { it.name }

        assertEquals(keyNames.size, keyNames.toSet().size, "Duplicate preference key names found")
    }
}
