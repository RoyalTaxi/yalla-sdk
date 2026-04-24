package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Contract for app configuration storage.
 *
 * Stores server-provided configuration values (support contacts,
 * payment limits, policy URLs). All properties are reactive [Flow]s
 * that emit the latest value whenever it changes.
 *
 * Implemented in the data module using Multiplatform Settings.
 *
 * ## Usage
 * ```kotlin
 * configPreferences.supportNumber
 *     .collect { number -> updateSupportButton(number) }
 * ```
 *
 * @see SessionPreferences
 * @see UserPreferences
 * @since 0.0.1
 */
interface ConfigPreferences {

    /** Phone number for customer support. */
    val supportNumber: Flow<String>

    /**
     * Persists the customer support phone number.
     *
     * @param value Phone number string
     */
    fun setSupportNumber(value: String)

    /** Telegram username/link for customer support. */
    val supportTelegram: Flow<String>

    /**
     * Persists the customer support Telegram contact.
     *
     * @param value Telegram username or deep link
     */
    fun setSupportTelegram(value: String)

    /** Instagram handle/link for company info. */
    val infoInstagram: Flow<String>

    /**
     * Persists the company Instagram contact.
     *
     * @param value Instagram username or deep link
     */
    fun setInfoInstagram(value: String)

    /** Telegram channel/link for company info and news. */
    val infoTelegram: Flow<String>

    /**
     * Persists the company Telegram info channel.
     *
     * @param value Telegram channel username or deep link
     */
    fun setInfoTelegram(value: String)

    /** Privacy policy URL for Russian locale. */
    val privacyPolicyRu: Flow<String>

    /**
     * Persists the Russian-language privacy policy URL.
     *
     * @param value Full URL to the privacy policy page
     */
    fun setPrivacyPolicyRu(value: String)

    /** Privacy policy URL for Uzbek locale. */
    val privacyPolicyUz: Flow<String>

    /**
     * Persists the Uzbek-language privacy policy URL.
     *
     * @param value Full URL to the privacy policy page
     */
    fun setPrivacyPolicyUz(value: String)

    /** Maximum bonus amount the user can apply to a single order. */
    val maxBonus: Flow<Long>

    /**
     * Persists the maximum bonus limit.
     *
     * @param value Maximum bonus in smallest currency unit
     */
    fun setMaxBonus(value: Long)

    /** Minimum bonus amount required to use bonus payment. */
    val minBonus: Flow<Long>

    /**
     * Persists the minimum bonus threshold.
     *
     * @param value Minimum bonus in smallest currency unit
     */
    fun setMinBonus(value: Long)

    /** User's current bonus balance in smallest currency unit. */
    val balance: Flow<Long>

    /**
     * Persists the user's bonus balance.
     *
     * @param value Balance in smallest currency unit
     */
    fun setBalance(value: Long)

    /** Whether bonus payment is enabled by the server. */
    val isBonusEnabled: Flow<Boolean>

    /**
     * Persists the bonus payment availability flag.
     *
     * @param value `true` if bonus payment is allowed
     */
    fun setBonusEnabled(value: Boolean)

    /** Whether card payment is enabled by the server. */
    val isCardEnabled: Flow<Boolean>

    /**
     * Persists the card payment availability flag.
     *
     * @param value `true` if card payment is allowed
     */
    fun setCardEnabled(value: Boolean)

    /** Grace period (in seconds) before an order cancellation incurs a fee. */
    val orderCancelTime: Flow<Int>

    /**
     * Persists the free cancellation window duration.
     *
     * @param value Duration in seconds
     */
    fun setOrderCancelTime(value: Int)
}
