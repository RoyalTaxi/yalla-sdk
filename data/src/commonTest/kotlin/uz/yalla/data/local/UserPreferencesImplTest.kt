package uz.yalla.data.local

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import uz.yalla.core.payment.PaymentKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Unit tests for [UserPreferencesImpl].
 *
 * Uses [UnconfinedTestDispatcher] so `scope.launch { dataStore.edit { ... } }`
 * runs eagerly — by the time a setter returns, the in-memory DataStore has
 * observed the write.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesImplTest {

    @Test
    fun shouldReturnEmptyFirstNameOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals("", impl.firstName.first())
    }

    @Test
    fun shouldPropagateSetFirstNameToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setFirstName("Islom")

        assertEquals("Islom", impl.firstName.first())
    }

    @Test
    fun shouldReturnEmptyLastNameOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals("", impl.lastName.first())
    }

    @Test
    fun shouldPropagateSetLastNameToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setLastName("Sheraliyev")

        assertEquals("Sheraliyev", impl.lastName.first())
    }

    @Test
    fun shouldReturnEmptyNumberOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals("", impl.number.first())
    }

    @Test
    fun shouldPropagateSetNumberToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setNumber("+998901234567")

        assertEquals("+998901234567", impl.number.first())
    }

    @Test
    fun shouldDefaultToCashPaymentOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals(PaymentKind.Cash, impl.paymentType.first())
    }

    @Test
    fun shouldPersistCardPaymentWithIdentifiers() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setPaymentType(PaymentKind.Card(cardId = "card-42", maskedNumber = "**** 1234"))

        val readBack = impl.paymentType.first()
        val card = assertIs<PaymentKind.Card>(readBack)
        assertEquals("card-42", card.cardId)
        assertEquals("**** 1234", card.maskedNumber)
    }

    @Test
    fun shouldRemoveCardFieldsWhenSwitchingBackToCash() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)
        impl.setPaymentType(PaymentKind.Card(cardId = "card-42", maskedNumber = "**** 1234"))

        impl.setPaymentType(PaymentKind.Cash)

        assertEquals(PaymentKind.Cash, impl.paymentType.first())
    }

    @Test
    fun shouldOverwritePreviousCardOnSuccessiveSets() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)
        impl.setPaymentType(PaymentKind.Card(cardId = "old", maskedNumber = "**** 0000"))

        impl.setPaymentType(PaymentKind.Card(cardId = "new", maskedNumber = "**** 9999"))

        val card = assertIs<PaymentKind.Card>(impl.paymentType.first())
        assertEquals("new", card.cardId)
        assertEquals("**** 9999", card.maskedNumber)
    }

    private fun newImpl(scope: TestScope): UserPreferencesImpl = UserPreferencesImpl(
        dataStore = InMemoryDataStore(),
        scope = scope.backgroundScope,
    )
}
