package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.ConfigPreferences
import uz.yalla.core.util.or0
import uz.yalla.core.util.orFalse

/**
 * [DataStore]-backed implementation of [ConfigPreferences].
 *
 * Manages server-provided configuration: support contacts, social links,
 * privacy policy URLs, bonus limits, and balance.
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class ConfigPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : ConfigPreferences {
    override val supportNumber: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.SUPPORT_NUMBER].orEmpty() }

    override fun setSupportNumber(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.SUPPORT_NUMBER] = value } }
    }

    override val supportTelegram: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.SUPPORT_TELEGRAM].orEmpty() }

    override fun setSupportTelegram(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.SUPPORT_TELEGRAM] = value } }
    }

    override val infoInstagram: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.INFO_INSTAGRAM].orEmpty() }

    override fun setInfoInstagram(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.INFO_INSTAGRAM] = value } }
    }

    override val infoTelegram: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.INFO_TELEGRAM].orEmpty() }

    override fun setInfoTelegram(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.INFO_TELEGRAM] = value } }
    }

    override val privacyPolicyRu: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.PRIVACY_POLICY_RU].orEmpty() }

    override fun setPrivacyPolicyRu(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.PRIVACY_POLICY_RU] = value } }
    }

    override val privacyPolicyUz: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.PRIVACY_POLICY_UZ].orEmpty() }

    override fun setPrivacyPolicyUz(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.PRIVACY_POLICY_UZ] = value } }
    }

    override val maxBonus: Flow<Long> =
        dataStore.data.map { it.getLongSafe(PreferenceKeys.MAX_BONUS) }

    override fun setMaxBonus(value: Long) {
        scope.launch { dataStore.edit { it[PreferenceKeys.MAX_BONUS] = value } }
    }

    override val minBonus: Flow<Long> =
        dataStore.data.map { it.getLongSafe(PreferenceKeys.MIN_BONUS) }

    override fun setMinBonus(value: Long) {
        scope.launch { dataStore.edit { it[PreferenceKeys.MIN_BONUS] = value } }
    }

    override val balance: Flow<Long> =
        dataStore.data.map { it.getLongSafe(PreferenceKeys.BALANCE) }

    override fun setBalance(value: Long) {
        scope.launch { dataStore.edit { it[PreferenceKeys.BALANCE] = value } }
    }

    override val isBonusEnabled: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.IS_BONUS_ENABLED].orFalse() }

    override fun setBonusEnabled(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_BONUS_ENABLED] = value } }
    }

    override val isCardEnabled: Flow<Boolean> =
        dataStore.data.map { it[PreferenceKeys.IS_CARD_ENABLED].orFalse() }

    override fun setCardEnabled(value: Boolean) {
        scope.launch { dataStore.edit { it[PreferenceKeys.IS_CARD_ENABLED] = value } }
    }

    override val orderCancelTime: Flow<Int> =
        dataStore.data.map { it[PreferenceKeys.ORDER_CANCEL_TIME].or0() }

    override fun setOrderCancelTime(value: Int) {
        scope.launch { dataStore.edit { it[PreferenceKeys.ORDER_CANCEL_TIME] = value } }
    }
}

/**
 * Safely reads a Long preference that may have been stored as Int
 * by an older app version, avoiding ClassCastException.
 */
private fun Preferences.getLongSafe(key: Preferences.Key<Long>): Long =
    try {
        this[key] ?: 0L
    } catch (_: ClassCastException) {
        0L
    }
