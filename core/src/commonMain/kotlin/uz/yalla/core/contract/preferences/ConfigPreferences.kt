package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow

interface ConfigPreferences {
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
}
