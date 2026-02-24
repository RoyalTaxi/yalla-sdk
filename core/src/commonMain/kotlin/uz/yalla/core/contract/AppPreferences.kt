package uz.yalla.core.contract

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.kind.LocaleKind
import uz.yalla.core.kind.MapKind
import uz.yalla.core.kind.PaymentKind
import uz.yalla.core.kind.ThemeKind

interface AppPreferences {
    val localeType: Flow<LocaleKind>

    fun setLocaleType(value: LocaleKind)

    val accessToken: Flow<String>

    fun setAccessToken(value: String)

    val firebaseToken: Flow<String>

    fun setFirebaseToken(value: String)

    val firstName: Flow<String>

    fun setFirstName(value: String)

    val lastName: Flow<String>

    fun setLastName(value: String)

    val number: Flow<String>

    fun setNumber(value: String)

    val mapType: Flow<MapKind>

    fun setMapType(value: MapKind)

    val paymentType: Flow<PaymentKind>

    fun setPaymentType(value: PaymentKind)

    val supportNumber: Flow<String>

    fun setSupportNumber(value: String)

    val supportTelegram: Flow<String>

    fun setSupportTelegram(value: String)

    val infoInstagram: Flow<String>

    fun setInfoInstagram(value: String)

    val infoTelegram: Flow<String>

    fun setInfoTelegram(value: String)

    val privacyPolicyRu: Flow<String>

    fun setPrivacyPolicyRu(value: String)

    val privacyPolicyUz: Flow<String>

    fun setPrivacyPolicyUz(value: String)

    val referralLink: Flow<String>

    fun setReferralLink(value: String)

    val becomeDrive: Flow<String>

    fun setBecomeDrive(value: String)

    val inviteFriends: Flow<String>

    fun setInviteFriends(value: String)

    val lastAccessedLocation: Flow<Pair<Double, Double>>

    fun setLastAccessedLocation(
        lat: Double,
        lng: Double
    )

    val lastKnownLocation: Flow<Pair<Double, Double>>

    fun setLastKnownLocation(
        lat: Double,
        lng: Double
    )

    val maxBonus: Flow<Long>

    fun setMaxBonus(value: Long)

    val minBonus: Flow<Long>

    fun setMinBonus(value: Long)

    val balance: Flow<Long>

    fun setBalance(value: Long)

    val isBonusEnabled: Flow<Boolean>

    fun setBonusEnabled(value: Boolean)

    val isCardEnabled: Flow<Boolean>

    fun setCardEnabled(value: Boolean)

    val orderCancelTime: Flow<Int>

    fun setOrderCancelTime(value: Int)

    val isVerificationRequired: Flow<Boolean>

    fun setIsVerificationRequired(value: Boolean)

    val themeType: Flow<ThemeKind>

    fun setThemeType(value: ThemeKind)

    fun performLogout()
}
