package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import uz.yalla.core.identity.CardId
import uz.yalla.core.payment.PaymentMethod
import uz.yalla.core.preferences.UserPreferences

internal class UserPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val secureStore: SecureStore,
    private val scope: CoroutineScope
) : UserPreferences {
    // Profile PII is encrypted at rest (CWE-312): name/phone/birthday/gender flow through SecureStore.
    override val firstName: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.FIRST_NAME.name, secureStore)

    override fun setFirstName(value: String) {
        dataStore.secureWrite(PreferenceKeys.FIRST_NAME.name, value, secureStore, scope)
    }

    override val lastName: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.LAST_NAME.name, secureStore)

    override fun setLastName(value: String) {
        dataStore.secureWrite(PreferenceKeys.LAST_NAME.name, value, secureStore, scope)
    }

    override val number: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.NUMBER.name, secureStore)

    override fun setNumber(value: String) {
        dataStore.secureWrite(PreferenceKeys.NUMBER.name, value, secureStore, scope)
    }

    override val image: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.IMAGE.name, secureStore)

    override fun setImage(value: String) {
        dataStore.secureWrite(PreferenceKeys.IMAGE.name, value, secureStore, scope)
    }

    override val gender: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.GENDER.name, secureStore)

    override fun setGender(value: String) {
        dataStore.secureWrite(PreferenceKeys.GENDER.name, value, secureStore, scope)
    }

    override val birthday: Flow<String> =
        dataStore.secureReadFlow(PreferenceKeys.BIRTHDAY.name, secureStore)

    override fun setBirthday(value: String) {
        dataStore.secureWrite(PreferenceKeys.BIRTHDAY.name, value, secureStore, scope)
    }

    // The card id + masked PAN are payment identifiers (encrypted); PAYMENT_TYPE is a plain "cash"/"card"
    // discriminator. Combine the two secure flows so paymentMethod re-emits when either card field changes.
    override val paymentMethod: Flow<PaymentMethod> =
        combine(
            dataStore.readFlow { it[PreferenceKeys.PAYMENT_TYPE] },
            dataStore.secureReadFlow(PreferenceKeys.CARD_ID.name, secureStore),
            dataStore.secureReadFlow(PreferenceKeys.CARD_NUMBER.name, secureStore)
        ) { type, cardId, maskedNumber ->
            PaymentMethod.from(
                id = type,
                cardId = cardId.takeIf { it.isNotEmpty() }?.let { CardId(it) },
                maskedNumber = maskedNumber
            )
        }.distinctUntilChanged()

    override fun setPaymentMethod(value: PaymentMethod) {
        // Ordered in one coroutine so the encrypted card fields are settled BEFORE the plain "card"/"cash"
        // discriminator flips — otherwise the combined paymentMethod flow could momentarily reconstruct a
        // half-written method. Mirrors the original single-edit atomicity across the two stores.
        scope.launch {
            if (value is PaymentMethod.Card) {
                dataStore.secureSet(PreferenceKeys.CARD_ID.name, value.cardId.raw, secureStore)
                dataStore.secureSet(PreferenceKeys.CARD_NUMBER.name, value.maskedNumber, secureStore)
            } else {
                dataStore.secureUnset(PreferenceKeys.CARD_ID.name, secureStore)
                dataStore.secureUnset(PreferenceKeys.CARD_NUMBER.name, secureStore)
            }
            dataStore.edit { it[PreferenceKeys.PAYMENT_TYPE] = value.id }
        }
    }
}
