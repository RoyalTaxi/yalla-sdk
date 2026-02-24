package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.AppPreferences
import uz.yalla.core.contract.StaticPreferences
import uz.yalla.core.kind.LocaleKind
import uz.yalla.core.kind.MapKind
import uz.yalla.core.kind.PaymentKind
import uz.yalla.core.kind.ThemeKind
import uz.yalla.core.util.or0
import uz.yalla.core.util.orFalse

class AppPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val staticPreferences: StaticPreferences
) : AppPreferences {
    private val scope = CoroutineScope(Dispatchers.Default)

    private object Keys {
        val LOCALE_TYPE = stringPreferencesKey("localeType")
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        val FIREBASE_TOKEN = stringPreferencesKey("firebaseToken")
        val FIRST_NAME = stringPreferencesKey("firstName")
        val LAST_NAME = stringPreferencesKey("lastName")
        val NUMBER = stringPreferencesKey("number")
        val MAP_TYPE = stringPreferencesKey("mapType")
        val PAYMENT_TYPE = stringPreferencesKey("paymentType")
        val CARD_ID = stringPreferencesKey("cardId")
        val CARD_NUMBER = stringPreferencesKey("cardNumber")
        val SUPPORT_NUMBER = stringPreferencesKey("supportNumber")
        val SUPPORT_TELEGRAM = stringPreferencesKey("supportTelegram")
        val INFO_INSTAGRAM = stringPreferencesKey("infoInstagram")
        val INFO_TELEGRAM = stringPreferencesKey("infoTelegram")
        val PRIVACY_POLICY_RU = stringPreferencesKey("privacyPolicyRu")
        val PRIVACY_POLICY_UZ = stringPreferencesKey("privacyPolicyUz")
        val REFERRAL_LINK = stringPreferencesKey("referralLink")
        val BECOME_DRIVE = stringPreferencesKey("becomeDrive")
        val INVITE_FRIENDS = stringPreferencesKey("inviteFriends")
        val LAST_ACCESSED_LOCATION = stringPreferencesKey("lastAccessedLocation")
        val LAST_KNOWN_LOCATION = stringPreferencesKey("lastKnownLocation")
        val MAX_BONUS = longPreferencesKey("maxBonus")
        val MIN_BONUS = longPreferencesKey("minBonus")
        val BALANCE = longPreferencesKey("balance")
        val IS_BONUS_ENABLED = booleanPreferencesKey("isBonusEnabled")
        val IS_CARD_ENABLED = booleanPreferencesKey("isCardEnabled")
        val ORDER_CANCEL_TIME = intPreferencesKey("orderCancelTime")
        val IS_VERIFICATION_REQUIRED = booleanPreferencesKey("isVerificationRequired")
        val THEME_TYPE = stringPreferencesKey("themeType")
    }

    override val localeType: Flow<LocaleKind> =
        dataStore.data.map { prefs ->
            prefs[Keys.LOCALE_TYPE]?.let { LocaleKind.from(it) } ?: staticPreferences.localeType
        }

    override fun setLocaleType(value: LocaleKind) {
        staticPreferences.localeType = value
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.LOCALE_TYPE] = value.code
            }
        }
    }

    override val accessToken: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.ACCESS_TOKEN].orEmpty()
        }

    override fun setAccessToken(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.ACCESS_TOKEN] = value
            }
        }
    }

    override val firebaseToken: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.FIREBASE_TOKEN].orEmpty()
        }

    override fun setFirebaseToken(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.FIREBASE_TOKEN] = value
            }
        }
    }

    override val firstName: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.FIRST_NAME].orEmpty()
        }

    override fun setFirstName(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.FIRST_NAME] = value
            }
        }
    }

    override val lastName: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.LAST_NAME].orEmpty()
        }

    override fun setLastName(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.LAST_NAME] = value
            }
        }
    }

    override val number: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.NUMBER].orEmpty()
        }

    override fun setNumber(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.NUMBER] = value
            }
        }
    }

    override val mapType: Flow<MapKind> =
        dataStore.data.map { prefs ->
            val id = prefs[Keys.MAP_TYPE] ?: MapKind.Google.id
            MapKind.from(id)
        }

    override fun setMapType(value: MapKind) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.MAP_TYPE] = value.id
            }
        }
    }

    override val paymentType: Flow<PaymentKind> =
        dataStore.data.map { prefs ->
            val id = prefs[Keys.PAYMENT_TYPE] ?: PaymentKind.Cash.id
            val cardId = prefs[Keys.CARD_ID].orEmpty()
            val cardNumber = prefs[Keys.CARD_NUMBER].orEmpty()
            if (id == "card" && cardId.isNotEmpty()) {
                PaymentKind.Card(cardId, cardNumber)
            } else {
                PaymentKind.Cash
            }
        }

    override fun setPaymentType(value: PaymentKind) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.PAYMENT_TYPE] = value.id
                if (value is PaymentKind.Card) {
                    prefs[Keys.CARD_ID] = value.cardId
                    prefs[Keys.CARD_NUMBER] = value.maskedNumber
                } else {
                    prefs.remove(Keys.CARD_ID)
                    prefs.remove(Keys.CARD_NUMBER)
                }
            }
        }
    }

    override val supportNumber: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.SUPPORT_NUMBER].orEmpty()
        }

    override fun setSupportNumber(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.SUPPORT_NUMBER] = value
            }
        }
    }

    override val supportTelegram: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.SUPPORT_TELEGRAM].orEmpty()
        }

    override fun setSupportTelegram(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.SUPPORT_TELEGRAM] = value
            }
        }
    }

    override val infoInstagram: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.INFO_INSTAGRAM].orEmpty()
        }

    override fun setInfoInstagram(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.INFO_INSTAGRAM] = value
            }
        }
    }

    override val infoTelegram: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.INFO_TELEGRAM].orEmpty()
        }

    override fun setInfoTelegram(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.INFO_TELEGRAM] = value
            }
        }
    }

    override val privacyPolicyRu: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.PRIVACY_POLICY_RU].orEmpty()
        }

    override fun setPrivacyPolicyRu(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.PRIVACY_POLICY_RU] = value
            }
        }
    }

    override val privacyPolicyUz: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.PRIVACY_POLICY_UZ].orEmpty()
        }

    override fun setPrivacyPolicyUz(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.PRIVACY_POLICY_UZ] = value
            }
        }
    }

    override val referralLink: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.REFERRAL_LINK].orEmpty()
        }

    override fun setReferralLink(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.REFERRAL_LINK] = value
            }
        }
    }

    override val becomeDrive: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.BECOME_DRIVE].orEmpty()
        }

    override fun setBecomeDrive(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.BECOME_DRIVE] = value
            }
        }
    }

    override val inviteFriends: Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.INVITE_FRIENDS].orEmpty()
        }

    override fun setInviteFriends(value: String) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.INVITE_FRIENDS] = value
            }
        }
    }

    override val lastAccessedLocation: Flow<Pair<Double, Double>> =
        dataStore.data.map { prefs ->
            parseLocation(prefs[Keys.LAST_ACCESSED_LOCATION])
        }

    override fun setLastAccessedLocation(
        lat: Double,
        lng: Double
    ) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.LAST_ACCESSED_LOCATION] = "$lat,$lng"
            }
        }
    }

    override val lastKnownLocation: Flow<Pair<Double, Double>> =
        dataStore.data.map { prefs ->
            parseLocation(
                raw = prefs[Keys.LAST_KNOWN_LOCATION],
                fallbackRaw = prefs[Keys.LAST_ACCESSED_LOCATION]
            )
        }

    override fun setLastKnownLocation(
        lat: Double,
        lng: Double
    ) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.LAST_KNOWN_LOCATION] = "$lat,$lng"
            }
        }
    }

    override val maxBonus: Flow<Long> =
        dataStore.data.map { prefs ->
            prefs[Keys.MAX_BONUS].or0()
        }

    override fun setMaxBonus(value: Long) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.MAX_BONUS] = value
            }
        }
    }

    override val minBonus: Flow<Long> =
        dataStore.data.map { prefs ->
            prefs[Keys.MIN_BONUS].or0()
        }

    override fun setMinBonus(value: Long) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.MIN_BONUS] = value
            }
        }
    }

    override val balance: Flow<Long> =
        dataStore.data.map { prefs ->
            prefs[Keys.BALANCE].or0()
        }

    override fun setBalance(value: Long) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.BALANCE] = value
            }
        }
    }

    override val isBonusEnabled: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[Keys.IS_BONUS_ENABLED].orFalse()
        }

    override fun setBonusEnabled(value: Boolean) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.IS_BONUS_ENABLED] = value
            }
        }
    }

    override val isCardEnabled: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[Keys.IS_CARD_ENABLED].orFalse()
        }

    override fun setCardEnabled(value: Boolean) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.IS_CARD_ENABLED] = value
            }
        }
    }

    override val orderCancelTime: Flow<Int> =
        dataStore.data.map { prefs ->
            prefs[Keys.ORDER_CANCEL_TIME].or0()
        }

    override fun setOrderCancelTime(value: Int) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.ORDER_CANCEL_TIME] = value
            }
        }
    }

    override val isVerificationRequired: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[Keys.IS_VERIFICATION_REQUIRED].orFalse()
        }

    override fun setIsVerificationRequired(value: Boolean) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.IS_VERIFICATION_REQUIRED] = value
            }
        }
    }

    override val themeType: Flow<ThemeKind> =
        dataStore.data.map { prefs ->
            ThemeKind.from(prefs[Keys.THEME_TYPE])
        }

    override fun setThemeType(value: ThemeKind) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.THEME_TYPE] = value.id
            }
        }
    }

    override fun performLogout() {
        scope.launch {
            dataStore.edit { prefs ->
                prefs.clear()
            }
        }
    }
}

private fun parseLocation(
    raw: String?,
    fallbackRaw: String? = null
): Pair<Double, Double> {
    val source = raw?.takeIf { it.isNotBlank() } ?: fallbackRaw.orEmpty()
    val parts = source.split(",", limit = 2)
    val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
    val lng = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
    return lat to lng
}
