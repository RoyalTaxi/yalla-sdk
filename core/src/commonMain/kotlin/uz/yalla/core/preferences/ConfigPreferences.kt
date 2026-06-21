package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Support and public contact channels from the backend app configuration snapshot.
 */
public interface SupportConfigPreferences {
    public val supportNumber: Flow<String>

    public fun setSupportNumber(value: String)

    public val supportTelegram: Flow<String>

    public fun setSupportTelegram(value: String)

    public val infoInstagram: Flow<String>

    public fun setInfoInstagram(value: String)

    public val infoTelegram: Flow<String>

    public fun setInfoTelegram(value: String)
}

/** Legal document URLs from the backend app configuration snapshot. */
public interface LegalConfigPreferences {
    public val privacyPolicyRu: Flow<String>

    public fun setPrivacyPolicyRu(value: String)

    public val privacyPolicyUz: Flow<String>

    public fun setPrivacyPolicyUz(value: String)
}

/** Bonus, balance, and payment policy values from the backend app configuration snapshot. */
public interface BonusConfigPreferences {
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
}

/** Order lifecycle policy values from the backend app configuration snapshot. */
public interface OrderConfigPreferences {
    public val orderCancelTime: Flow<Int>

    public fun setOrderCancelTime(value: Int)
}

/**
 * Full remote app configuration mirrored to local storage as one cohesive backend snapshot.
 *
 * Prefer the narrower parent interfaces when a consumer needs only one slice of configuration.
 */
public interface ConfigPreferences :
    SupportConfigPreferences,
    LegalConfigPreferences,
    BonusConfigPreferences,
    OrderConfigPreferences
