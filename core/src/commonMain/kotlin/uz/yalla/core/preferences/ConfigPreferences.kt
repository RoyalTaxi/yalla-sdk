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
 */
interface ConfigPreferences {

    // Support contact channels — shown on the "Help" screen, writable by support staff.

    val supportNumber: Flow<String>
    fun setSupportNumber(value: String)

    val supportTelegram: Flow<String>
    fun setSupportTelegram(value: String)

    // Public marketing channels — shown on the "About" screen.

    val infoInstagram: Flow<String>
    fun setInfoInstagram(value: String)

    val infoTelegram: Flow<String>
    fun setInfoTelegram(value: String)

    // Privacy policy URLs, one per supported locale.

    val privacyPolicyRu: Flow<String>
    fun setPrivacyPolicyRu(value: String)

    val privacyPolicyUz: Flow<String>
    fun setPrivacyPolicyUz(value: String)

    /** Maximum bonus amount the user can apply to a single order. */
    val maxBonus: Flow<Long>
    fun setMaxBonus(value: Long)

    /** Minimum bonus amount required to use bonus payment. */
    val minBonus: Flow<Long>
    fun setMinBonus(value: Long)

    /** User's current bonus balance in smallest currency unit. */
    val balance: Flow<Long>
    fun setBalance(value: Long)

    /** Whether bonus payment is enabled by the server. */
    val isBonusEnabled: Flow<Boolean>
    fun setBonusEnabled(value: Boolean)

    /** Whether card payment is enabled by the server. */
    val isCardEnabled: Flow<Boolean>
    fun setCardEnabled(value: Boolean)

    /** Grace period (in seconds) before an order cancellation incurs a fee. */
    val orderCancelTime: Flow<Int>
    fun setOrderCancelTime(value: Int)
}
