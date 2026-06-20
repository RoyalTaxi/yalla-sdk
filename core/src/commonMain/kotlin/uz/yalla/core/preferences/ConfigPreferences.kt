package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Remote app configuration mirrored to local storage as one cohesive snapshot (support/info contacts,
 * legal URLs, bonus economics, balance, card/order policy).
 *
 * Intentionally wider than the segregated [PositionPreferences]/[SessionPreferences]/[InterfacePreferences]
 * (ISP): all of it is fetched, persisted, and invalidated together as a single backend "app config"
 * payload, so splitting it would fragment one atomic refresh across several stores for no consumer gain.
 */
// TODO(quality, needs-decision): M14 — if a finer split is wanted, it is a breaking .api change with
// external consumers; needs owner sign-off. Documented as deliberately-wide for now.
public interface ConfigPreferences {
    public val supportNumber: Flow<String>

    public fun setSupportNumber(value: String)

    public val supportTelegram: Flow<String>

    public fun setSupportTelegram(value: String)

    public val infoInstagram: Flow<String>

    public fun setInfoInstagram(value: String)

    public val infoTelegram: Flow<String>

    public fun setInfoTelegram(value: String)

    public val privacyPolicyRu: Flow<String>

    public fun setPrivacyPolicyRu(value: String)

    public val privacyPolicyUz: Flow<String>

    public fun setPrivacyPolicyUz(value: String)

    public val maxBonus: Flow<Long>

    public fun setMaxBonus(value: Long)

    public val minBonus: Flow<Long>

    public fun setMinBonus(value: Long)

    public val balance: Flow<Long>

    public fun setBalance(value: Long)

    public val isBonusEnabled: Flow<Boolean>

    public fun setBonusEnabled(value: Boolean)

    public val isCardEnabled: Flow<Boolean>

    public fun setCardEnabled(value: Boolean)

    public val orderCancelTime: Flow<Int>

    public fun setOrderCancelTime(value: Int)
}
