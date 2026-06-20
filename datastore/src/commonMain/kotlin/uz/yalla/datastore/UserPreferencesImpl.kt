package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.identity.CardId
import uz.yalla.core.payment.PaymentMethod
import uz.yalla.core.preferences.UserPreferences

internal class UserPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) : UserPreferences {
    override val firstName: Flow<String> = dataStore.readFlow { it[PreferenceKeys.FIRST_NAME].orEmpty() }

    override fun setFirstName(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.FIRST_NAME] = value }
    }

    override val lastName: Flow<String> = dataStore.readFlow { it[PreferenceKeys.LAST_NAME].orEmpty() }

    override fun setLastName(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.LAST_NAME] = value }
    }

    override val number: Flow<String> = dataStore.readFlow { it[PreferenceKeys.NUMBER].orEmpty() }

    override fun setNumber(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.NUMBER] = value }
    }

    override val image: Flow<String> = dataStore.readFlow { it[PreferenceKeys.IMAGE].orEmpty() }

    override fun setImage(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.IMAGE] = value }
    }

    override val gender: Flow<String> = dataStore.readFlow { it[PreferenceKeys.GENDER].orEmpty() }

    override fun setGender(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.GENDER] = value }
    }

    override val birthday: Flow<String> = dataStore.readFlow { it[PreferenceKeys.BIRTHDAY].orEmpty() }

    override fun setBirthday(value: String) {
        dataStore.write(scope) { it[PreferenceKeys.BIRTHDAY] = value }
    }

    override val paymentMethod: Flow<PaymentMethod> =
        dataStore.readFlow { prefs ->
            PaymentMethod.from(
                id = prefs[PreferenceKeys.PAYMENT_TYPE],
                cardId = prefs[PreferenceKeys.CARD_ID]?.let { CardId(it) },
                maskedNumber = prefs[PreferenceKeys.CARD_NUMBER]
            )
        }

    override fun setPaymentMethod(value: PaymentMethod) {
        dataStore.write(scope) { prefs ->
            prefs[PreferenceKeys.PAYMENT_TYPE] = value.id
            if (value is PaymentMethod.Card) {
                prefs[PreferenceKeys.CARD_ID] = value.cardId.raw
                prefs[PreferenceKeys.CARD_NUMBER] = value.maskedNumber
            } else {
                prefs.remove(PreferenceKeys.CARD_ID)
                prefs.remove(PreferenceKeys.CARD_NUMBER)
            }
        }
    }
}
