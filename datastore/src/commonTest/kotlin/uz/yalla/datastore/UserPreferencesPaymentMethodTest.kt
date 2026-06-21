package uz.yalla.datastore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import uz.yalla.core.identity.CardId
import uz.yalla.core.payment.PaymentMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Pins the module's only branch-heavy write: [UserPreferencesImpl.setPaymentMethod] persists card id +
 * masked number for a Card, and REMOVES them for Cash. Guards the switch-back path — Card then Cash must
 * leave no stale card data behind, or a later read reconstructs a wrong payment method for a billed ride.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesPaymentMethodTest {
    @Test
    fun cardRoundTripsWithItsIdAndMaskedNumber() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            val user = UserPreferencesImpl(store, secure, CoroutineScope(StandardTestDispatcher(testScheduler)))

            user.setPaymentMethod(PaymentMethod.Card(CardId("card-42"), "**** 1234"))
            advanceUntilIdle()

            val read = user.paymentMethod.first()
            assertEquals(PaymentMethod.Card(CardId("card-42"), "**** 1234"), read)
            // The card id + masked PAN are encrypted at rest, not in plain prefs.
            assertEquals("card-42", secure.peek(PreferenceKeys.CARD_ID.name))
            assertEquals("**** 1234", secure.peek(PreferenceKeys.CARD_NUMBER.name))
        }

    @Test
    fun switchingBackToCashClearsStaleCardData() =
        runTest {
            val store = InMemoryPreferencesDataStore()
            val secure = FakeSecureStore()
            val user = UserPreferencesImpl(store, secure, CoroutineScope(StandardTestDispatcher(testScheduler)))

            user.setPaymentMethod(PaymentMethod.Card(CardId("card-42"), "**** 1234"))
            advanceUntilIdle()
            user.setPaymentMethod(PaymentMethod.Cash)
            advanceUntilIdle()

            assertEquals(PaymentMethod.Cash, user.paymentMethod.first())
            // The encrypted card values must be gone, not merely shadowed — otherwise a future 'card' write
            // with leftover data could resurrect a stale card.
            assertNull(secure.peek(PreferenceKeys.CARD_ID.name))
            assertNull(secure.peek(PreferenceKeys.CARD_NUMBER.name))
        }
}
