package uz.yalla.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.preferences.UserPreferences
import uz.yalla.core.payment.PaymentKind

/**
 * [DataStore]-backed implementation of [UserPreferences].
 *
 * Manages user profile data: name, phone number, and payment method.
 *
 * @param dataStore shared preferences store
 * @param scope coroutine scope for write operations
 * @since 0.0.1
 */
internal class UserPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
) : UserPreferences {
    override val firstName: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.FIRST_NAME].orEmpty() }

    override fun setFirstName(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.FIRST_NAME] = value } }
    }

    override val lastName: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.LAST_NAME].orEmpty() }

    override fun setLastName(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.LAST_NAME] = value } }
    }

    override val number: Flow<String> =
        dataStore.data.map { it[PreferenceKeys.NUMBER].orEmpty() }

    override fun setNumber(value: String) {
        scope.launch { dataStore.edit { it[PreferenceKeys.NUMBER] = value } }
    }

    override val paymentType: Flow<PaymentKind> =
        dataStore.data.map { prefs ->
            val id = prefs[PreferenceKeys.PAYMENT_TYPE] ?: PaymentKind.Cash.id
            val cardId = prefs[PreferenceKeys.CARD_ID].orEmpty()
            val cardNumber = prefs[PreferenceKeys.CARD_NUMBER].orEmpty()
            if (id == "card" && cardId.isNotEmpty()) {
                PaymentKind.Card(cardId, cardNumber)
            } else {
                PaymentKind.Cash
            }
        }

    override fun setPaymentType(value: PaymentKind) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[PreferenceKeys.PAYMENT_TYPE] = value.id
                if (value is PaymentKind.Card) {
                    prefs[PreferenceKeys.CARD_ID] = value.cardId
                    prefs[PreferenceKeys.CARD_NUMBER] = value.maskedNumber
                } else {
                    prefs.remove(PreferenceKeys.CARD_ID)
                    prefs.remove(PreferenceKeys.CARD_NUMBER)
                }
            }
        }
    }
}
