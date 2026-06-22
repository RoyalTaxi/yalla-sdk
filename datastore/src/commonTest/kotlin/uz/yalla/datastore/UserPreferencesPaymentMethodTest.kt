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
            assertNull(secure.peek(PreferenceKeys.CARD_ID.name))
            assertNull(secure.peek(PreferenceKeys.CARD_NUMBER.name))
        }
}
