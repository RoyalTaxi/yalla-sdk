package uz.yalla.data.local

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [ConfigPreferencesImpl].
 *
 * Uses [UnconfinedTestDispatcher] so `scope.launch { dataStore.edit { ... } }`
 * runs eagerly — by the time a setter returns, the in-memory DataStore has
 * observed the write.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ConfigPreferencesImplTest {

    @Test
    fun shouldReturnEmptyStringsAndZerosOnColdRead() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        assertEquals("", impl.supportNumber.first())
        assertEquals("", impl.supportTelegram.first())
        assertEquals("", impl.infoInstagram.first())
        assertEquals("", impl.infoTelegram.first())
        assertEquals("", impl.privacyPolicyRu.first())
        assertEquals("", impl.privacyPolicyUz.first())
        assertEquals(0L, impl.maxBonus.first())
        assertEquals(0L, impl.minBonus.first())
        assertEquals(0L, impl.balance.first())
        assertFalse(impl.isBonusEnabled.first())
        assertFalse(impl.isCardEnabled.first())
        assertEquals(0, impl.orderCancelTime.first())
    }

    @Test
    fun shouldPropagateSupportNumberToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setSupportNumber("+998 71 200 00 00")

        assertEquals("+998 71 200 00 00", impl.supportNumber.first())
    }

    @Test
    fun shouldPropagateSupportTelegramToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setSupportTelegram("@yalla_support")

        assertEquals("@yalla_support", impl.supportTelegram.first())
    }

    @Test
    fun shouldPropagateInfoInstagramAndTelegramToFlows() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setInfoInstagram("https://instagram.com/yalla")
        impl.setInfoTelegram("https://t.me/yalla_info")

        assertEquals("https://instagram.com/yalla", impl.infoInstagram.first())
        assertEquals("https://t.me/yalla_info", impl.infoTelegram.first())
    }

    @Test
    fun shouldPropagatePrivacyPoliciesToFlows() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setPrivacyPolicyRu("https://yalla.uz/privacy/ru")
        impl.setPrivacyPolicyUz("https://yalla.uz/privacy/uz")

        assertEquals("https://yalla.uz/privacy/ru", impl.privacyPolicyRu.first())
        assertEquals("https://yalla.uz/privacy/uz", impl.privacyPolicyUz.first())
    }

    @Test
    fun shouldPropagateBonusLimitsAndBalanceToFlows() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setMaxBonus(50_000L)
        impl.setMinBonus(1_000L)
        impl.setBalance(12_345L)

        assertEquals(50_000L, impl.maxBonus.first())
        assertEquals(1_000L, impl.minBonus.first())
        assertEquals(12_345L, impl.balance.first())
    }

    @Test
    fun shouldPropagateBonusAndCardEnabledFlagsToFlows() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setBonusEnabled(true)
        impl.setCardEnabled(true)

        assertTrue(impl.isBonusEnabled.first())
        assertTrue(impl.isCardEnabled.first())
    }

    @Test
    fun shouldPropagateOrderCancelTimeToFlow() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)

        impl.setOrderCancelTime(180)

        assertEquals(180, impl.orderCancelTime.first())
    }

    @Test
    fun shouldOverwritePreviousBalanceOnSuccessiveSets() = runTest(UnconfinedTestDispatcher()) {
        val impl = newImpl(this)
        impl.setBalance(100L)

        impl.setBalance(250L)

        assertEquals(250L, impl.balance.first())
    }

    private fun newImpl(scope: TestScope): ConfigPreferencesImpl = ConfigPreferencesImpl(
        dataStore = InMemoryDataStore(),
        scope = scope.backgroundScope,
    )
}
