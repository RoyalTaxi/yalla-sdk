package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.preferences.ConfigPreferences
import uz.yalla.core.util.or0
import uz.yalla.core.util.orFalse

internal class ConfigPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : ConfigPreferences {
    override val supportNumber: Flow<String> = dataStore.readFlow { it[PreferenceKeys.SUPPORT_NUMBER].orEmpty() }

    override fun setSupportNumber(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.SUPPORT_NUMBER] = value }
    }

    override val supportTelegram: Flow<String> = dataStore.readFlow { it[PreferenceKeys.SUPPORT_TELEGRAM].orEmpty() }

    override fun setSupportTelegram(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.SUPPORT_TELEGRAM] = value }
    }

    override val infoInstagram: Flow<String> = dataStore.readFlow { it[PreferenceKeys.INFO_INSTAGRAM].orEmpty() }

    override fun setInfoInstagram(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.INFO_INSTAGRAM] = value }
    }

    override val infoTelegram: Flow<String> = dataStore.readFlow { it[PreferenceKeys.INFO_TELEGRAM].orEmpty() }

    override fun setInfoTelegram(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.INFO_TELEGRAM] = value }
    }

    override val privacyPolicyRu: Flow<String> = dataStore.readFlow { it[PreferenceKeys.PRIVACY_POLICY_RU].orEmpty() }

    override fun setPrivacyPolicyRu(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.PRIVACY_POLICY_RU] = value }
    }

    override val privacyPolicyUz: Flow<String> = dataStore.readFlow { it[PreferenceKeys.PRIVACY_POLICY_UZ].orEmpty() }

    override fun setPrivacyPolicyUz(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.PRIVACY_POLICY_UZ] = value }
    }

    override val maxBonus: Flow<Long> = dataStore.readFlow { it[PreferenceKeys.MAX_BONUS] ?: 0L }

    override fun setMaxBonus(value: Long) {
        dataStore.write(scope) { it[PreferenceKeys.MAX_BONUS] = value }
    }

    override val minBonus: Flow<Long> = dataStore.readFlow { it[PreferenceKeys.MIN_BONUS] ?: 0L }

    override fun setMinBonus(value: Long) {
        dataStore.write(scope) { it[PreferenceKeys.MIN_BONUS] = value }
    }

    override val balance: Flow<Long> = dataStore.readFlow { it[PreferenceKeys.BALANCE] ?: 0L }

    override fun setBalance(value: Long) {
        dataStore.write(scope) { it[PreferenceKeys.BALANCE] = value }
    }

    override val isBonusEnabled: Flow<Boolean> = dataStore.readFlow { it[PreferenceKeys.IS_BONUS_ENABLED].orFalse() }

    override fun setBonusEnabled(value: Boolean) {
        dataStore.write(scope) { it[PreferenceKeys.IS_BONUS_ENABLED] = value }
    }

    override val isCardEnabled: Flow<Boolean> = dataStore.readFlow { it[PreferenceKeys.IS_CARD_ENABLED].orFalse() }

    override fun setCardEnabled(value: Boolean) {
        dataStore.write(scope) { it[PreferenceKeys.IS_CARD_ENABLED] = value }
    }

    override val orderCancelTime: Flow<Int> = dataStore.readFlow { it[PreferenceKeys.ORDER_CANCEL_TIME].or0() }

    override fun setOrderCancelTime(value: Int) {
        dataStore.write(scope) { it[PreferenceKeys.ORDER_CANCEL_TIME] = value }
    }
}
